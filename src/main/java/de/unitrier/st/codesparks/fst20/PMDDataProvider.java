package de.unitrier.st.codesparks.fst20;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.IDataProvider;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
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

public class PMDDataProvider implements IDataProvider
{
    private final String projectPath;

    public PMDDataProvider(String projectPath)
    {
        this.projectPath = projectPath;
    }

    private List<RuleViolation> ruleViolations;

    @Override
    public boolean collectData()
    {
        PluginId id = PluginId.getId("de.unitrier.st.codesparks.fst20.pmd.demo");
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(id);
        assert plugin != null;
        final String pluginAbsolutePath = plugin.getPluginPath().toAbsolutePath().toString();
        CodeSparksLogger.addText("Plugin path=%s", pluginAbsolutePath);

        final PMDConfiguration pmdConfiguration = new PMDConfiguration();
//        pmdConfiguration.setInputPaths(projectPath);
//        pmdConfiguration.setMinimumPriority(RulePriority.MEDIUM);
        pmdConfiguration.setRuleSets("D:\\git\\fst20-moseler-codesparks-pmd-demo\\pmd\\om-rules.xml");
//        pmdConfiguration.setRuleSets("rulesets/java/quickstart.xml");
//        pmdConfiguration.setReportFormat("xml");

        final RuleSetFactory factory = RulesetsFactoryUtils.createFactory(pmdConfiguration);

        try
        {
            final List<DataSource> files = determineFiles(projectPath + File.separator + "src");
            RuleContext ctx = new RuleContext();

            ruleViolations = new ArrayList<>();

            ctx.getReport().addListener(new ThreadSafeReportListener()
            {
                @Override
                public void ruleViolationAdded(final RuleViolation ruleViolation)
                {
//                    final String packageName = ruleViolation.getPackageName();
//                    final String className = ruleViolation.getClassName();
//                    final String methodName = ruleViolation.getMethodName();
//                    final int beginLine = ruleViolation.getBeginLine();
//                    final int endLine = ruleViolation.getEndLine();
//                    final String filename = ruleViolation.getFilename();
//
//                    final String variableName = ruleViolation.getVariableName();
//
//                    final String identifier = filename + "[" + beginLine + ":" + endLine + "]";
//
//                    final PMDArtifact pmdArtifact = new PMDArtifact(packageName + "." + className + "." + methodName,
//                            identifier);
//
//                    final Rule rule = ruleViolation.getRule();
//                    final String description = ruleViolation.getDescription();
//                    CodeSparksLogger.addText(description);

                    ruleViolations.add(ruleViolation);

//
//                    final String[] s = description.split(" ");
//
//                    String metricValue;
//                    if (s.length == 9)
//                    {
//                        metricValue = s[8].substring(0, 1);
//                    } else
//                    {
//                        metricValue = s[9];
//                    }
//
////                    CodeSparksLogger.addText(metricValue);
//
//                    int value = Integer.parseInt(metricValue);
//                    pmdArtifact.setMetricValue(value);
//                    pmdArtifactPool.add(pmdArtifact);
                }

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

            PMD.processFiles(pmdConfiguration, factory, files, ctx, xmlRenderers);

            xmlRenderer.end();
            xmlRenderer.flush();

//            System.out.println(stringWriter.toString());
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
                } else
                {
//                    System.out.printf("Ignoring %s%n", path);
                }
                return super.visitFile(path, attrs);
            }
        });
        System.out.printf("Analyzing %d files in %s%n", files.size(), basePath);
        return files;
    }

    @Override
    public IArtifactPool processData()
    {
        final PMDArtifactPool pmdArtifactPool = new PMDArtifactPool();

        for (final RuleViolation ruleViolation : ruleViolations)
        {
            final String description = ruleViolation.getDescription();
            CodeSparksLogger.addText(description);

            final String packageName = ruleViolation.getPackageName();
            final String className = ruleViolation.getClassName();
            final String methodName = ruleViolation.getMethodName();
            final int beginLine = ruleViolation.getBeginLine();
            final int endLine = ruleViolation.getEndLine();
            final String filename = ruleViolation.getFilename();

            final String identifier = filename + "[" + beginLine + ":" + endLine + "]";

            final PMDArtifact pmdArtifact = new PMDArtifact(
                    packageName + "." + className + "." + methodName
                    , identifier
            );
            final String[] s = description.split(" ");

            String metricValue;
            if (s.length == 9)
            {
                metricValue = s[8].substring(0, 1);
            } else
            {
                metricValue = s[9];
            }

            int value = Integer.parseInt(metricValue);
            pmdArtifact.setMetricValue(value);
            pmdArtifactPool.add(pmdArtifact);
        }

        return pmdArtifactPool;
    }
}
