package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.ADataProvider;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ArtifactBuilder;
import de.unitrier.st.codesparks.core.data.DefaultArtifactPool;
import de.unitrier.st.codesparks.core.data.IArtifactPool;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class PMDDataProvider extends ADataProvider implements PMDMetrics
{
    private final String projectPath;

    public PMDDataProvider(final String projectPath)
    {
        this.projectPath = projectPath;
        this.ruleViolations = new ArrayList<>();
    }

    private final List<RuleViolation> ruleViolations;

    @Override
    public boolean collectData()
    {
        final String pluginAbsolutePath = CoreUtil.getAbsolutePluginPathString();

        final PMDConfiguration pmdConfiguration = new PMDConfiguration();
        pmdConfiguration.setMinimumPriority(RulePriority.MEDIUM);

        final Path pluginPath = CoreUtil.getPluginPath();
        if (pluginPath != null)
        {
            final String cacheLocation = pluginPath + File.separator + "pmd-analysis.cache";
            pmdConfiguration.setAnalysisCacheLocation(cacheLocation);
            CodeSparksLogger.addText("PMD analysis cache located at: %s", cacheLocation);
        }

        final String pmdRulesPath = pluginAbsolutePath
                + File.separator
                + "classes"
                + File.separator
                + "pmd"
                + File.separator
                + "codesparks-pmd-demo.xml";

        pmdConfiguration.setRuleSets(pmdRulesPath);

        final RuleSetFactory factory = RulesetsFactoryUtils.createFactory(pmdConfiguration);
        try
        {
            final List<DataSource> files = determineFiles(projectPath + File.separator + "src");
            final RuleContext ctx = new RuleContext();
            ctx.getReport().addListener(new ThreadSafeReportListener()
            {
                @Override
                public void ruleViolationAdded(final RuleViolation ruleViolation)
                {
                    synchronized (ruleViolations)
                    {
                        ruleViolations.add(ruleViolation);
                    }
                }

                @SuppressWarnings("deprecation")
                @Override
                public void metricAdded(final Metric metric)
                {
                    // ignored
                }
            });

            final StringWriter stringWriter = new StringWriter();
            final XMLRenderer xmlRenderer = new XMLRenderer("UTF-8");

            xmlRenderer.setWriter(stringWriter);
            xmlRenderer.start();

            final List<Renderer> xmlRenderers = Collections.singletonList(xmlRenderer);

            PMD.processFiles(pmdConfiguration, factory, files, ctx, xmlRenderers);

            xmlRenderer.end();
            xmlRenderer.flush();

        } catch (IOException e)
        {
            CodeSparksLogger.addText("%s: IO exception %s.", getClass().getName(), e.getMessage());
            return false;
        }
        return true;
    }

    private static List<DataSource> determineFiles(final String basePath) throws IOException
    {
        final Path dirPath = FileSystems.getDefault().getPath(basePath);
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");
        final List<DataSource> files = new ArrayList<>();
        Files.walkFileTree(dirPath, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException
            {
                if (matcher.matches(path.getFileName()))
                {
                    files.add(new FileDataSource(path.toFile()));
                }
                return super.visitFile(path, attrs);
            }
        });
        return files;
    }

    @Override
    public IArtifactPool processData()
    {
        final IArtifactPool pmdArtifactPool = new DefaultArtifactPool();

        for (final RuleViolation ruleViolation : ruleViolations)
        {
            final Rule rule = ruleViolation.getRule();
            final String ruleName = rule.getName();
            if ("CyclomaticComplexity".equals(ruleName))
            {
                final String packageName = ruleViolation.getPackageName();
                final String className = ruleViolation.getClassName();
                final String methodName = ruleViolation.getMethodName();

                String artifactDisplayName = packageName + "." + className;

                final Class<? extends AArtifact> artifactClass;
                boolean isClass = false;
                final String artifactName;
                if ("".equals(methodName))
                {
                    artifactName = className;
                    artifactClass = PMDClassArtifact.class;
                    isClass = true;
                } else
                {
                    artifactName = methodName;
                    artifactClass = PMDMethodArtifact.class;
                    artifactDisplayName += "." + methodName;
                }

                final int beginLine = ruleViolation.getBeginLine();
                final String filename = ruleViolation.getFilename();
                final String fixedFileName = FilenameUtils.separatorsToSystem(filename);
                final String artifactIdentifier = PMDArtifactUtil.getArtifactIdentifier(fixedFileName, artifactName, beginLine);
                final ArtifactBuilder artifactBuilder = new ArtifactBuilder(artifactIdentifier, artifactDisplayName, artifactClass);
                final AArtifact artifact = artifactBuilder
                        .setFileName(fixedFileName)
                        .setLineNumber(beginLine)
                        .get();

                final String description = ruleViolation.getDescription();
                final String[] split = description.split(" ");

                int metricValueIndex;
                if (isClass)
                {
                    if (description.contains("'lambda'"))
                    {
                        metricValueIndex = 1;
                    } else
                    {
                        metricValueIndex = 3;
                    }
                    String maxOfClass = split[split.length - 1];
                    maxOfClass = getSubStringUntilLastOf(maxOfClass, ")");
                    final Double maxOfClassValue = parseMetricValue(maxOfClass);
                    artifact.setNumericalMetricValue(CYCLO_MAX_OF_CLASS, maxOfClassValue);
                } else
                { // Methods
                    metricValueIndex = 1;
                }

                String metricValueString = split[split.length - metricValueIndex];
                metricValueString = getSubStringUntilLastOf(metricValueString, ".");
                double metricValue = parseMetricValue(metricValueString);

                artifact.setNumericalMetricValue(CYCLOMATIC_COMPLEXITY, metricValue);
                pmdArtifactPool.addArtifact(artifact);
            }
        }

        return pmdArtifactPool;
    }

    @Override
    public void postProcess(final IArtifactPool artifactPool)
    {
        final List<AArtifact> classArtifacts = artifactPool.getArtifacts(PMDClassArtifact.class);
        final List<Double> cycloValues =
                classArtifacts
                        .stream()
                        .map(artifact -> artifact.getNumericalMetricValue(CYCLOMATIC_COMPLEXITY))
                        .filter(val -> !val.isNaN())
                        .collect(Collectors.toList());
        final Optional<Double> reduce = cycloValues.stream().reduce(Double::sum);
        if (reduce.isPresent())
        {
            final int nrOfClassArtifacts = cycloValues.size();
            final Double sum = reduce.get();
            final double expectancyValue = sum / nrOfClassArtifacts; // Also the arithmetic average (mean).

            double quadSum = 0D;
            for (final Double cycloValue : cycloValues)
            {
                quadSum += (cycloValue - expectancyValue) * (cycloValue - expectancyValue);
            }
            double standardDeviation = Math.sqrt(quadSum / nrOfClassArtifacts);

            classArtifacts.forEach(artifact -> {
                artifact.setNumericalMetricValue(CYCLO_MEAN, expectancyValue);
                artifact.setNumericalMetricValue(CYCLO_SD, standardDeviation);
            });
        }
    }

    /*
     * Minor helpers.
     */
    private String getSubStringUntilLastOf(final String str, final String delimiter)
    {
        final int lastIndexOf = str.lastIndexOf(delimiter);
        if (lastIndexOf > 0)
        {
            return str.substring(0, lastIndexOf);
        }
        return str;
    }

    private Double parseMetricValue(final String metricValueString)
    {
        try
        {
            return Double.parseDouble(metricValueString);
        } catch (NumberFormatException e)
        {
            CodeSparksLogger.addText("Could not parse metric value to double: %s", e.getMessage());
            return Double.NaN;
        }
    }
}
