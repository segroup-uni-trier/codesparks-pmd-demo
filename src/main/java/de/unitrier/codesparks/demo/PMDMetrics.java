package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.ANumericMetricIdentifier;

public interface PMDMetrics
{
    AMetricIdentifier CYCLOMATIC_COMPLEXITY = new ANumericMetricIdentifier()
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
        public boolean isRelative()
        {
            return false;
        }
    };

    AMetricIdentifier CYCLO_MEAN = new ANumericMetricIdentifier() {
        @Override
        public String getIdentifier()
        {
            return "cyclomatic-complexity-mean";
        }

        @Override
        public String getName()
        {
            return "Cyclomatic-Complexity-Mean";
        }

        @Override
        public String getDisplayString()
        {
            return "Cyclomatic Complexity Mean";
        }

        @Override
        public String getShortDisplayString()
        {
            return "Cyclo-Mean";
        }

        @Override
        public boolean isRelative()
        {
            return false;
        }
    };

    AMetricIdentifier CYCLO_SD = new ANumericMetricIdentifier() {
        @Override
        public String getIdentifier()
        {
            return "cyclomatic-complexity-standard-deviation";
        }

        @Override
        public String getName()
        {
            return "Cyclomatic-Complexity-Standard-Deviation";
        }

        @Override
        public String getDisplayString()
        {
            return "Cyclomatic Complexity Standard Deviation";
        }

        @Override
        public String getShortDisplayString()
        {
            return "Cyclo-SD";
        }

        @Override
        public boolean isRelative()
        {
            return false;
        }
    };

}
