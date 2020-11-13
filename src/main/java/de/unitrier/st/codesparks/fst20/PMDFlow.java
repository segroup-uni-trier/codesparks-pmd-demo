package de.unitrier.st.codesparks.fst20;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.ACodeSparksFlow;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.DefaultArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.DefaultDataVisualizer;

public class PMDFlow extends ACodeSparksFlow
{
    public PMDFlow(final Project project)
    {
        super(project);

        final String basePath = project.getBasePath();

        dataProvider = new PMDDataProvider(basePath);

        matcher = new PMDArtifactPoolToCodeMatcher();

        dataVisualizer = new DefaultDataVisualizer(new AArtifactVisualizationLabelFactory[]{
//                new DummyArtifactVisualizationLabelFactory(),
                new DefaultArtifactVisualizationLabelFactory()
        });

    }
}
