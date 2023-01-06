/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.unitrier.st.codesparks.demo;

import de.unitrier.st.codesparks.core.data.ANumericMetricIdentifier;

public interface PMDMetrics
{
    ANumericMetricIdentifier CYCLOMATIC_COMPLEXITY = new ANumericMetricIdentifier()
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

    ANumericMetricIdentifier CYCLO_MEAN = new ANumericMetricIdentifier() {
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

    ANumericMetricIdentifier CYCLO_SD = new ANumericMetricIdentifier() {
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

    ANumericMetricIdentifier CYCLO_MAX_OF_CLASS = new ANumericMetricIdentifier() {
        @Override
        public String getIdentifier()
        {
            return "cyclomatic-complexity-max-of-class";
        }

        @Override
        public String getName()
        {
            return "Cyclomatic-Complexity-Max-of-Class";
        }

        @Override
        public String getDisplayString()
        {
            return "Cyclomatic Complexity: Max of class";
        }

        @Override
        public String getShortDisplayString()
        {
            return "Cyclo-Max-of-Class";
        }

        @Override
        public boolean isRelative()
        {
            return false;
        }
    };

}
