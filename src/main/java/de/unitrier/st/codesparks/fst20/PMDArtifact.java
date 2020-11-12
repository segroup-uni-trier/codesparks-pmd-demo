package de.unitrier.st.codesparks.fst20;

import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;

import java.util.List;
import java.util.Map;

public class PMDArtifact extends AArtifact
{
    protected PMDArtifact(final String name, final String identifier)
    {
        super(name, identifier);

    }

    @Override
    public String getTitleName()
    {
        return name;
    }

    @Override
    public Map<String, List<ACodeSparksThread>> getThreadTypeLists()
    {
        return null;
    }

    @Override
    public String getDisplayString(final int maxLen)
    {
        return CoreUtil.reduceToLength(name, maxLen);
    }

    @Override
    public String getDisplayString()
    {
        return name;
    }

    @Override
    public void navigate()
    {

    }
}
