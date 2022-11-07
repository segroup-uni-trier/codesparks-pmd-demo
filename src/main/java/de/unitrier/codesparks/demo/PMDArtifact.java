package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.java.JavaArtifactNavigationUtil;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

abstract class PMDArtifact extends AArtifact
{
    protected boolean isClass;

    PMDArtifact(final String identifier, final String name)
    {
        super(identifier, name);
    }

    @Override
    public boolean navigate()
    {
        if (!super.navigate())
        {
            //return JavaArtifactNavigationUtil.navigateToLineInClass(name, lineNumber);
            final boolean navigate = JavaArtifactNavigationUtil.navigateToLineInFile(fileName, lineNumber);
            if (!navigate)
            {
                CodeSparksLogger.addText("Could not navigate to artifact: %s", identifier);
            }
            return navigate;
        }
        return true;
    }
}
