package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.DefaultArtifactPool;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.IDataProvider;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ArtifactBuilder;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.service.CodeSparksInstanceService;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PMDDataProvider implements IDataProvider, PMDMetrics
{
    private final String projectPath;

    public PMDDataProvider(String projectPath)
    {
        this.projectPath = projectPath;
        this.ruleViolations = new ArrayList<>();
    }

    private final List<RuleViolation> ruleViolations;

    @Override
    public boolean collectData()
    {
        final CodeSparksInstanceService service = CodeSparksInstanceService.getInstance();
        final String pluginAbsolutePath = service.getPluginPath().toAbsolutePath().toString();

        final PMDConfiguration pmdConfiguration = new PMDConfiguration();
        pmdConfiguration.setMinimumPriority(RulePriority.MEDIUM);

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

            /*
            Alternatively hijack the pmd library to retrieve the metric values directly! For advanced purposes only!
             */

//            final RuleSetFactory ruleSetFactory = RulesetsFactoryUtils.defaultFactory();
//            final RuleSet singleRuleRuleSet = ruleSetFactory.createSingleRuleRuleSet(new CycloRule());
//
//            for (final DataSource file : files)
//            {
//                final String realFileName = file.getNiceFileName(false, null);
//                final LanguageVersion languageVersionOfFile = pmdConfiguration.getLanguageVersionOfFile(realFileName);
//                final Parser parser = PMD.parserFor(languageVersionOfFile, pmdConfiguration);
//                final InputStream sourceCode = new BufferedInputStream(file.getInputStream());
//                final Reader streamReader = new InputStreamReader(sourceCode, pmdConfiguration.getSourceEncoding());
//                final Node root = parser.parse(realFileName, streamReader);
//                ctx.setLanguageVersion(languageVersionOfFile);
//                singleRuleRuleSet.apply(Collections.singletonList(root), ctx);
//            }
        } catch (IOException e)
        {
            e.printStackTrace();
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
        pmdArtifactPool.registerArtifactClassDisplayNameProvider(artifactClass -> {
            if (artifactClass.equals(PMDArtifact.class))
            {
                return "PMD artifacts";
            }
            return artifactClass.getSimpleName();
        });
        for (final RuleViolation ruleViolation : ruleViolations)
        {
            final String description = ruleViolation.getDescription();
            CodeSparksLogger.addText(description);

            final String packageName = ruleViolation.getPackageName();
            final String className = ruleViolation.getClassName();
            final String methodName = ruleViolation.getMethodName();
            final int beginLine = ruleViolation.getBeginLine();
            final String filename = ruleViolation.getFilename();

            String name = packageName + "." + className;
            if (!"".equals(methodName))
            {
                name += "." + methodName;
            }

            final String idName;
            if ("".equals(methodName))
            {
                idName = className;
            } else
            {
                idName = methodName;
            }

            final String artifactIdentifier = PMDArtifactUtil.getArtifactIdentifier(filename, idName, beginLine);

            final String[] s = description.split(" ");

            String metricValue = "n/a";
            for (int i = 0; i < s.length; i++)
            {
                String str = s[i];
                if ("of".equals(str))
                {
                    metricValue = s[i + 1];
                    break;
                }
            }

            if (metricValue.endsWith("."))
            {
                metricValue = metricValue.substring(0, 1);
            }

            int value = -1;
            try
            {
                value = Integer.parseInt(metricValue);
            } catch (NumberFormatException e)
            {
                // ignored
            }

            final ArtifactBuilder artifactBuilder = new ArtifactBuilder(artifactIdentifier, name, PMDArtifact.class);
            final AArtifact artifact = artifactBuilder
                    .setFileName(filename)
                    .setLineNumber(beginLine)
                    .setNumericMetricValue(CYCLOMATIC_COMPLEXITY, value)
                    .get();
            pmdArtifactPool.addArtifact(artifact);
        }
        return pmdArtifactPool;
    }
}
