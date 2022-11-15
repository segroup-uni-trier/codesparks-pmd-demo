/*
 * Copyright (c) 2022. Oliver Moseler
 */

package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;

import javax.swing.*;

public class CycloClassArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    protected CycloClassArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier, PMDClassArtifact.class);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        return null;
    }
}
