package de.unitrier.st.fst20.codesparks;

import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.DefaultArtifactVisualizationMouseListener;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static com.intellij.ui.JBColor.BLACK;
import static de.unitrier.st.codesparks.core.visualization.VisConstants.*;

public class CycloArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    protected CycloArtifactVisualizationLabelFactory(final String primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    @Override
    public JLabel createArtifactLabel(@NotNull AArtifact artifact)
    {
        int lineHeight = VisConstants.getLineHeight();

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, 5000, lineHeight,
                BufferedImage.TYPE_INT_RGB, PaintUtil.RoundingMode.CEIL);

        Graphics graphics = bi.getGraphics();

        Color backgroundColor = VisualizationUtil.getSelectedFileEditorBackgroundColor();

        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());

        int selfBarHeight = 2;
        final int X_OFFSET = VisConstants.X_OFFSET;
        final int Y_OFFSET = selfBarHeight + 1;

        Rectangle artifactVisualizationArea = new Rectangle(X_OFFSET, Y_OFFSET, RECTANGLE_WIDTH, lineHeight - 1 - selfBarHeight);

        /*
         * Draw the intensity rectangle
         */
        int metricValue = (int) artifact.getNumericalMetricValue(primaryMetricIdentifier);
        Color green = Color.decode("#1C9109");
        Color orange = Color.decode("#AF570B");
        Color red = Color.decode("#AF0B0B");
        Color metricColor = metricValue < 5 ? green : metricValue < 10 ? orange : red;
        graphics.setColor(metricColor);
        VisualizationUtil.fillRectangle(graphics, artifactVisualizationArea);
        /*
         * Draw the secondary metric
         */
        int selfWidth = 0;

        graphics.drawLine(X_OFFSET + selfWidth, 0, X_OFFSET + RECTANGLE_WIDTH, 0);
        graphics.drawLine(X_OFFSET + selfWidth, 0, X_OFFSET + RECTANGLE_WIDTH, 0);
        graphics.setColor(BORDER_COLOR);
        graphics.drawRect(X_OFFSET, Y_OFFSET, RECTANGLE_WIDTH, lineHeight - Y_OFFSET - 1);
        /*
         * Draw the text
         */

        String text = "Cyclo: " + metricValue;

        double textWidth = graphics.getFontMetrics().stringWidth(text);
        graphics.setColor(BLACK);
        Font font = new Font("Arial", Font.BOLD, 11);  // TODO: support different font sizes
        graphics.setFont(font);


        Color textColor = VisualizationUtil.getTextColor(metricColor);
        graphics.setColor(textColor);
        graphics.drawString(text, X_OFFSET + 1 + (int) ((RECTANGLE_WIDTH / 2d) - (textWidth / 2d)),
                Y_OFFSET + (int) ((lineHeight - Y_OFFSET) * .75d));
        graphics.setColor(STANDARD_FONT_COLOR);

        /*
         * Set the actual image icon size
         */
        int actualIconWidth = X_OFFSET + RECTANGLE_WIDTH + 4 * CALLEE_TRIANGLES_WIDTH + 1;
        BufferedImage subImage = bi.getSubimage(0, 0, actualIconWidth, bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subImage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
//        jLabel.addMouseListener(new DefaultArtifactVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier, null));

        return jLabel;
    }
}
