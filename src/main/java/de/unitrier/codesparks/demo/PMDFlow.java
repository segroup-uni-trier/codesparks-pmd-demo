package de.unitrier.codesparks.demo;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.ACodeSparksFlow;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.java.JavaCurrentFileArtifactFilter;
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

        registerArtifactMetricComparatorForSorting(PMDArtifact.class, new ArtifactMetricComparator(CYCLOMATIC_COMPLEXITY, true));

        registerArtifactClassVisualizationLabelFactory(PMDArtifact.class, new CycloArtifactVisualizationLabelFactory(CYCLOMATIC_COMPLEXITY));

        PropertiesUtil.setPropertyValue(PropertiesFile.USER_INTERFACE_PROPERTIES,
                PropertyKey.THREAD_VISUALIZATIONS_ENABLED, false);

        final String basePath = project.getBasePath();

        dataProvider = new PMDDataProvider(basePath);

        matcher = new PMDArtifactPoolToCodeMatcher();

        dataVisualizer = new DefaultDataVisualizer(new AArtifactVisualizationLabelFactory[]{
                new CycloArtifactVisualizationLabelFactory(CYCLOMATIC_COMPLEXITY)
        });

    }
}
