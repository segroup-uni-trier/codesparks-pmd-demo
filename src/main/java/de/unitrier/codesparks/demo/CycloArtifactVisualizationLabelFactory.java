package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static de.unitrier.st.codesparks.core.visualization.VisConstants.BORDER_COLOR;

final class CycloArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    CycloArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    private static final Color GREEN = Color.decode("#1C9109");
    private static final Color ORANGE = Color.decode("#AF570B");
    private static final Color RED = Color.decode("#AF0B0B");

    @Override
    public JLabel createArtifactLabel(@NotNull final AArtifact artifact)
    {
        final int lineHeight = VisConstants.getLineHeight();
        final int X_OFFSET = 6;
        final int Y_OFFSET = 2;
        final int width = 70;
        final int totalWidth = width + 2 * X_OFFSET;
        final CodeSparksGraphics graphics = getGraphics(totalWidth, lineHeight);
        /*
         * Draw the intensity rectangle
         */
        final Rectangle artifactVisualizationArea = new Rectangle(X_OFFSET, Y_OFFSET, width, lineHeight - Y_OFFSET);
        final int metricValue = (int) artifact.getNumericalMetricValue(primaryMetricIdentifier);
        final Color metricColor = metricValue < 5 ? GREEN : metricValue < 10 ? ORANGE : RED;
        graphics.fillRectangle(artifactVisualizationArea, metricColor);

        final Rectangle frame = new Rectangle(X_OFFSET, Y_OFFSET, width, lineHeight - Y_OFFSET - 1);
        graphics.drawRectangle(frame, BORDER_COLOR);
        /*
         * Draw the text
         */
        final String text = primaryMetricIdentifier.getShortDisplayString() + ": " + metricValue;
        final Font font = new Font("Arial", Font.BOLD, 11);
        graphics.setFont(font);
        final Color textColor = VisualizationUtil.getTextColor(metricColor);
        final double textWidth = graphics.stringWidth(text);
        final int textXPos = X_OFFSET + 1 + (int) ((width / 2d) - (textWidth / 2d));
        final int textYPos = Y_OFFSET + (int) ((lineHeight - Y_OFFSET) * .75d);
        graphics.drawString(text, textXPos, textYPos, textColor);

        //noinspection UnnecessaryLocalVariable
        final JLabel jLabel = makeLabel(graphics);
        return jLabel;
    }
}
