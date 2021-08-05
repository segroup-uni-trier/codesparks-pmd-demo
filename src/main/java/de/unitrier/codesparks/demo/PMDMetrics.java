package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

public interface PMDMetrics
{
    AMetricIdentifier CYCLOMATIC_COMPLEXITY = new AMetricIdentifier()
    {
        @Override
        public String getIdentifier()
        {
            return "cyclomatic-complexity";
        }

        @Override
        public String getName()
        {
            return "Cyclomatic-Complexity";
        }

        @Override
        public String getDisplayString()
        {
            return "Cyclomatic Complexity";
        }

        @Override
        public String getShortDisplayString()
        {
            return "Cyclo";
        }

        @Override
        public boolean isNumerical()
        {
            return true;
        }

        @Override
        public boolean isRelative()
        {
            return false;
        }
    };
}
