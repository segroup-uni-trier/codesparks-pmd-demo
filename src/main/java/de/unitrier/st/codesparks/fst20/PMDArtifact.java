package de.unitrier.st.codesparks.fst20;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;

import java.util.List;
import java.util.Map;

public class PMDArtifact extends AArtifact
{
    public PMDArtifact(String name, String identifier)
    {
        super(name, identifier);
    }

    @Override
    public Map<String, List<ACodeSparksThread>> getThreadTypeLists()
    {
        return null;
    }

    @Override
    public void navigate()
    {

    }
}
