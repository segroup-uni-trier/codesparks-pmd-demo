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

public class PMDFlow extends ACodeSparksFlow implements PMDMetrics
{
    public PMDFlow(final Project project)
    {
        super(project);

        registerCurrentFileArtifactFilter(JavaCurrentFileArtifactFilter.getInstance(AArtifact::getName));

        final ArtifactMetricComparator artifactMetricComparator = new ArtifactMetricComparator(CYCLOMATIC_COMPLEXITY, true);

//        final CycloArtifactVisualizationLabelFactory cycloArtifactVisualizationLabelFactory =
//                new CycloArtifactVisualizationLabelFactory(CYCLOMATIC_COMPLEXITY, CYCLO_MAX_OF_CLASS, PMDClassArtifact.class, PMDMethodArtifact.class);
        // Alternative:
        final CycloArtifactVisualizationLabelFactory cycloArtifactVisualizationLabelFactory =
                new CycloArtifactVisualizationLabelFactory(CYCLOMATIC_COMPLEXITY, CYCLO_MAX_OF_CLASS);


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

        PropertiesUtil.setPropertyValue(PropertiesFile.USER_INTERFACE_PROPERTIES,
                PropertyKey.THREAD_VISUALIZATIONS_ENABLED, false);

        final String basePath = project.getBasePath();

        dataProvider = new PMDDataProvider(basePath);

//        matcher = new PMDArtifactPoolToCodeMatcher();
        matcher = new FileAndLineBasedJavaArtifactPoolToCodeMatcher();

        dataVisualizer = new DefaultDataVisualizer(new AArtifactVisualizationLabelFactory[]{
                cycloArtifactVisualizationLabelFactory
        });
    }
}
