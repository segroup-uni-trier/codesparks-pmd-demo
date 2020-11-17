package de.unitrier.st.codesparks.fst20;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import de.unitrier.st.codesparks.core.IArtifactClassDisplayNameProvider;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.IDataProvider;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ArtifactBuilder;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.service.ACodeSparksInstanceService;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PMDDataProvider implements IDataProvider
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
        final ACodeSparksInstanceService service = ServiceManager.getService(ACodeSparksInstanceService.class);
        final String pluginIdString = service.getPluginIdString();
        final PluginId id = PluginId.getId(pluginIdString);
        final IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(id);
        assert plugin != null;
        final String pluginAbsolutePath = plugin.getPluginPath().toAbsolutePath().toString();


        final PMDConfiguration pmdConfiguration = new PMDConfiguration();
//        pmdConfiguration.setInputPaths(projectPath);
        pmdConfiguration.setMinimumPriority(RulePriority.MEDIUM);

        final String pmdRulesPath = pluginAbsolutePath
                + File.separator
                + "classes"
                + File.separator
                + "pmd"
                + File.separator
                + "om-rules.xml";

        pmdConfiguration.setRuleSets(pmdRulesPath);
//        pmdConfiguration.setRuleSets("rulesets/java/quickstart.xml");
//        pmdConfiguration.setReportFormat("xml");

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

//            Report report = ctx.getReport();
//            List<RuleViolation> violations = report.getViolations();
//
//            CodeSparksLogger.addText("Number of violations = " + violations.size());

            xmlRenderer.end();
            xmlRenderer.flush();

//            System.out.println(stringWriter.toString());

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
        Path dirPath = FileSystems.getDefault().getPath(basePath);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");

        List<DataSource> files = new ArrayList<>();

        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException
            {
                if (matcher.matches(path.getFileName()))
                {
//                    System.out.printf("Using %s%n", path);
                    files.add(new FileDataSource(path.toFile()));
                }
                /*
                else
                {
                    System.out.printf("Ignoring %s%n", path);
                }
                */
                return super.visitFile(path, attrs);
            }
        });
//        System.out.printf("Analyzing %d files in %s%n", files.size(), basePath);
        return files;
    }

    @Override
    public IArtifactPool processData()
    {
        final PMDArtifactPool pmdArtifactPool = new PMDArtifactPool();

        pmdArtifactPool.registerArtifactClassDisplayNameProvider(new IArtifactClassDisplayNameProvider() {
            @Override
            public String getDisplayName(Class<? extends AArtifact> artifactClass)
            {
                if (artifactClass.equals(PMDArtifact.class))
                {
                    return "PMD Cyclo";
                }
                return artifactClass.getSimpleName();
            }
        });

        for (final RuleViolation ruleViolation : ruleViolations)
        {
            final String description = ruleViolation.getDescription();
//            CodeSparksLogger.addText(description);

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

            final ArtifactBuilder artifactBuilder = new ArtifactBuilder(name, artifactIdentifier, PMDArtifact.class);

            final AArtifact artifact = artifactBuilder
                    .setFileName(filename)
                    .setLineNumber(beginLine)
                    .setMetricValue(value)
                    .get();

            pmdArtifactPool.addArtifact(artifact);

//            pmdArtifactPool.add(artifact);
        }

        return pmdArtifactPool;
    }
}
