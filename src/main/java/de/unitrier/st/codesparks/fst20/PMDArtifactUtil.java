package de.unitrier.st.codesparks.fst20;

final class PMDArtifactUtil
{
    private PMDArtifactUtil() {}

    static String getArtifactIdentifier(final String fileName, final String artifactName, final int lineNumber)
    {
        return String.format("%s:%s@%d", fileName, artifactName, lineNumber);
    }

}
