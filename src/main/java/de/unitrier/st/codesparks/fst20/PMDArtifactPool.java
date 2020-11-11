package de.unitrier.st.codesparks.fst20;

import de.unitrier.st.codesparks.core.AArtifactPool;
import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PMDArtifactPool extends AArtifactPool
{
    private final Map<String, PMDArtifact> artifacts;

    PMDArtifactPool()
    {
        artifacts = new HashMap<>();
    }

    @Override
    public AArtifact getArtifact(final String identifier)
    {
        synchronized (artifacts)
        {
            return artifacts.get(identifier);
        }
    }

    @Override
    public List<AArtifact> getArtifacts()
    {
        synchronized (artifacts)
        {
            return new ArrayList<>(artifacts.values());
        }
    }

    @Override
    public Map<String, List<AArtifact>> getNamedArtifactTypeLists()
    {
        return null;
    }

    void add(final PMDArtifact pmdArtifact)
    {
        synchronized (artifacts)
        {
            artifacts.put(pmdArtifact.getIdentifier(), pmdArtifact);
        }
    }
}
