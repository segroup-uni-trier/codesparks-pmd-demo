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

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.navigation.ArtifactNavigationUtil;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

abstract class PMDArtifact extends AArtifact
{
    PMDArtifact(final String identifier, final String name)
    {
        super(identifier, name);
    }

    @Override
    public boolean navigate()
    {
        if (!super.navigate())
        {
            final boolean navigate = ArtifactNavigationUtil.navigateToLineInFile(fileName, lineNumber);
            if (!navigate)
            {
                CodeSparksLogger.addText("Could not navigate to artifact: %s", identifier);
            }
            return navigate;
        }
        return true;
    }
}
