package de.unitrier.codesparks.demo;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.ACodeSparksFlow;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.overview.ArtifactMetricComparator;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.DefaultArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.DefaultDataVisualizer;
import de.unitrier.st.codesparks.java.FileAndLineBasedJavaArtifactPoolToCodeMatcher;
import de.unitrier.st.codesparks.java.JavaCurrentFileArtifactFilter;
import de.unitrier.st.codesparks.java.JavaStandardLibraryFilter;

import java.awt.*;

public class PMDFlow extends ACodeSparksFlow implements PMDMetrics
{
    public PMDFlow(final Project project)
    {
        super(project);

        // Create an instance of the label factory.
        final CycloArtifactVisualizationLabelFactory cycloArtifactVisualizationLabelFactory =
                new CycloArtifactVisualizationLabelFactory(
                        CYCLOMATIC_COMPLEXITY, // The primary metric identifier.
                        CYCLO_MAX_OF_CLASS, // Add another metric identifier.
                        PMDClassArtifact.class, // Apply this visualization for class artifacts ...
                        PMDMethodArtifact.class // and method artifacts.
                );
        // Alternative. If the artifact classes are not specified, the label factory applies to each artifact type present in the artifact pool:
        /*
        final CycloArtifactVisualizationLabelFactory cycloArtifactVisualizationLabelFactory =
                new CycloArtifactVisualizationLabelFactory(CYCLOMATIC_COMPLEXITY, CYCLO_MAX_OF_CLASS);
        */
        // An example using the default label factory.
        final AArtifactVisualizationLabelFactory simpleCycloArtifactVisualizationLabelFactoryForMethods =
                new DefaultArtifactVisualizationLabelFactory( // A visualization for ...
                        CYCLOMATIC_COMPLEXITY, // ... the cyclomatic complexity ...
                        0, // ... at first position (left-most) in case there are multiple label factories registered ...
                        0, // ... with an x-offset of 0 to the previous visualization (left) ...
                        metricValue -> { // ... which applies this coloring strategy ...
                            final int metricIntValue = ((Double) metricValue).intValue();
                            Color metricColor;
                            if (metricIntValue < 11)
                                metricColor = Color.decode("#fef0d9");
                            else
                            {
                                if (metricIntValue < 21)
                                    metricColor = Color.decode("#fdcc8a");
                                else
                                {
                                    if (metricIntValue < 51)
                                        metricColor = Color.decode("#fc8d59");
                                    else
                                        metricColor = Color.decode("#d7301f");
                                }
                            }
                            return metricColor;
                        },
                        PMDMethodArtifact.class // ... and only applies for method artifacts.
                );

        /*
         * Configuring the Overview Window.
         */
        registerCurrentFileArtifactFilter(JavaCurrentFileArtifactFilter.getInstance(AArtifact::getName));
        registerStandardLibraryArtifactFilter(JavaStandardLibraryFilter.getInstance());

        final ArtifactMetricComparator artifactMetricComparator = new ArtifactMetricComparator(CYCLOMATIC_COMPLEXITY, true);
        registerArtifactMetricComparatorForSorting(PMDMethodArtifact.class, artifactMetricComparator);
        registerArtifactMetricComparatorForSorting(PMDClassArtifact.class, artifactMetricComparator);
        registerArtifactClassVisualizationLabelFactory(PMDMethodArtifact.class, cycloArtifactVisualizationLabelFactory);
        registerArtifactClassVisualizationLabelFactory(PMDClassArtifact.class, cycloArtifactVisualizationLabelFactory);
        registerArtifactClassDisplayNameProvider(artifactClass -> {
            if (artifactClass.equals(PMDMethodArtifact.class))
            {
                return "PMD method artifacts";
            }
            if (artifactClass.equals(PMDClassArtifact.class))
            {
                return "PMD class artifacts";
            }
            return artifactClass.getSimpleName();
        });

        /*
         * Disable the thread filter area in the Artifact Overview Window.
         */
        PropertiesUtil.setPropertyValue(PropertiesFile.USER_INTERFACE_PROPERTIES, PropertyKey.OVERVIEW_WINDOW_THREAD_FILTER_AREA_VISIBLE, false);

        /*
         * Set up all interfaces that implement the four phases of the CodeSparks flow.
         */
        final String basePath = project.getBasePath();

        final PMDDataProvider pmdDataProvider = new PMDDataProvider(basePath); // Implements and combines the data integration and processing phase.

        dataCollector = pmdDataProvider.getDataCollector();

        dataProcessor = pmdDataProvider.getDataProcessor();

        matcher = new FileAndLineBasedJavaArtifactPoolToCodeMatcher(); // The matching phase. Included in the core library.

        dataVisualizer = new DefaultDataVisualizer(new AArtifactVisualizationLabelFactory[]{ // Instantiate a data visualizer. Register label factories.
                cycloArtifactVisualizationLabelFactory
//                ,
//                simpleCycloArtifactVisualizationLabelFactoryForMethods
        });
    }
}
