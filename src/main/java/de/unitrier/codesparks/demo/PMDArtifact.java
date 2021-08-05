package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.data.AArtifact;

/*
Must be public because it might be called via reflection.
 */
public final class PMDArtifact extends AArtifact
{
    public PMDArtifact(final String identifier, final String name)
    {
        super(identifier, name);
    }
}
