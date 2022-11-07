package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.java.JavaMethodArtifact;

/*
 * Must be public because it might be called via reflection.
 */
@JavaMethodArtifact
public final class PMDMethodArtifact extends PMDArtifact
{
    public PMDMethodArtifact(final String identifier, final String name)
    {
        super(identifier, name);
    }
}
