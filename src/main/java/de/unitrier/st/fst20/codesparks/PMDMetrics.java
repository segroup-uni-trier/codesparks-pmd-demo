package de.unitrier.st.fst20.codesparks;

import de.unitrier.st.codesparks.core.data.IMetricIdentifier;

public interface PMDMetrics
{
    IMetricIdentifier CYCLOMATIC_COMPLEXITY = new IMetricIdentifier()
    {
        @Override
        public String getName()
        {
            return "Cyclomatic-Complexity";
        }

        @Override
        public String getDisplayString()
        {
            return "Cyclo";
        }

        @Override
        public boolean isNumerical()
        {
            return true;
        }
    };
}
