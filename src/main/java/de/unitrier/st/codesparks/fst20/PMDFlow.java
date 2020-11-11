package de.unitrier.st.codesparks.fst20;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.ACodeSparksFlow;

public class PMDFlow extends ACodeSparksFlow
{
    public PMDFlow(final Project project)
    {
        super(project);

        final String basePath = project.getBasePath();

        dataProvider = new PMDDataProvider(basePath);
    }
}
