package de.unitrier.codesparks.demo;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.ACodeSparksFlow;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.java.FileAndLineBasedJavaArtifactPoolToCodeMatcher;
import de.unitrier.st.codesparks.java.JavaCurrentFileArtifactFilter;
import de.unitrier.st.codesparks.core.overview.ArtifactMetricComparator;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.DefaultDataVisualizer;
import de.unitrier.st.codesparks.java.JavaStandardLibraryFilter;

public class PMDFlow extends ACodeSparksFlow implements PMDMetrics
{
    public PMDFlow(final Project project)
    {
        super(project);

        /*
         * Create an instance of the label factory.
         */
        //final CycloArtifactVisualizationLabelFactory cycloArtifactVisualizationLabelFactory =
//                new CycloArtifactVisualizationLabelFactory(CYCLOMATIC_COMPLEXITY, CYCLO_MAX_OF_CLASS, PMDClassArtifact.class, PMDMethodArtifact.class);
        // Alternative:
        final CycloArtifactVisualizationLabelFactory cycloArtifactVisualizationLabelFactory =
                new CycloArtifactVisualizationLabelFactory(CYCLOMATIC_COMPLEXITY, CYCLO_MAX_OF_CLASS);

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

        dataProvider = new PMDDataProvider(basePath); // Implements and combines the data integration and processing phase.

        matcher = new FileAndLineBasedJavaArtifactPoolToCodeMatcher(); // The matching phase. Included in the core library.

        dataVisualizer = new DefaultDataVisualizer(new AArtifactVisualizationLabelFactory[]{ // Instantiate a data visualizer. Register label factories.
                cycloArtifactVisualizationLabelFactory
        });
    }
}
