package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANumericMetricIdentifier;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.java.JavaUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static de.unitrier.st.codesparks.core.visualization.VisConstants.BORDER_COLOR;

final class CycloArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    private final ANumericMetricIdentifier secondaryMetricIdentifier;

    CycloArtifactVisualizationLabelFactory(
            final ANumericMetricIdentifier primaryMetricIdentifier,
            final ANumericMetricIdentifier secondaryMetricIdentifier
    )
    {
        super(primaryMetricIdentifier);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
    }

    @SafeVarargs
    CycloArtifactVisualizationLabelFactory(
            final ANumericMetricIdentifier primaryMetricIdentifier,
            final ANumericMetricIdentifier secondaryMetricIdentifier,
            final Class<? extends AArtifact>... artifactClasses
    )
    {
        super(primaryMetricIdentifier, artifactClasses);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
    }

    // https://colorbrewer2.org/#type=sequential&scheme=PuRd&n=4
    private static final Color SIMPLE_COLOR = Color.decode("#feebe2");
    private static final Color MORE_COMPLEX_COLOR = Color.decode("#fbb4b9");
    private static final Color COMPLEX_COLOR = Color.decode("#f768a1");
    private static final Color UNTESTABLE_COLOR = Color.decode("#ae017e");

    @Override
    public JLabel createArtifactLabel(@NotNull final AArtifact artifact)
    {
        final int metricValue = (int) artifact.getNumericalMetricValue(primaryMetricIdentifier);

        final boolean isMethodArtifact = JavaUtil.isMethodArtifact(artifact);
        final boolean isClassArtifact = JavaUtil.isClassArtifact(artifact);

        Color metricColor;
        String toolTipText;

        if (isMethodArtifact)
        {
            if (metricValue < 11)
            {
                metricColor = SIMPLE_COLOR;
                toolTipText = "Simple, little risk.";
            } else
            {
                if (metricValue < 21)
                {
                    metricColor = MORE_COMPLEX_COLOR;
                    toolTipText = "More complex, moderate risk.";
                } else
                {
                    if (metricValue < 51)
                    {
                        metricColor = COMPLEX_COLOR;
                        toolTipText = "Complex, high risk.";
                    } else
                    {
                        metricColor = UNTESTABLE_COLOR;
                        toolTipText = "Untestable code, very high risk.";
                    }
                }
            }
            toolTipText = String.format("Cyclomatic Complexity: %d >> %s", metricValue, toolTipText);
        } else
        { // Any other type, e.g. a class. Use statistical thresholds according to: Lanza, Marinescu: OO-metrics in practice
            final double cycloMean = artifact.getNumericalMetricValue(PMDMetrics.CYCLO_MEAN);
            final double cycloSD = artifact.getNumericalMetricValue(PMDMetrics.CYCLO_SD);
            String explanation = "sum of methods";
            if (isClassArtifact)
            {
                final int maxOfClass = (int) artifact.getNumericalMetricValue(secondaryMetricIdentifier);
                explanation += ", max=" + maxOfClass;
            }

            final String scope = "compared to other classes in the project";
            double lowThreshold = cycloMean - cycloSD;
            double highThreshold = cycloMean + cycloSD;
            double veryHighThreshold = (cycloMean + cycloSD) * 1.5;

            final String interpretation;
            if (metricValue < lowThreshold)
            {
                metricColor = SIMPLE_COLOR;
                interpretation = "Low";
            } else
            {
                if (metricValue < highThreshold)
                {
                    metricColor = MORE_COMPLEX_COLOR;
                    interpretation = "Average";
                } else
                {
                    if (metricValue < veryHighThreshold)
                    {
                        metricColor = COMPLEX_COLOR;
                        interpretation = "High";
                    } else
                    {
                        metricColor = UNTESTABLE_COLOR;
                        interpretation = "Very high";
                    }
                }
            }
            toolTipText = String.format("Cyclomatic Complexity: %d (%s) >> %s %s.", metricValue, explanation, interpretation, scope);
        }

        /*
         * Prepare the text
         */
        String text = primaryMetricIdentifier.getShortDisplayString() + ": " + metricValue;
        if (isClassArtifact)
        {
            final int maxOfClass = (int) artifact.getNumericalMetricValue(secondaryMetricIdentifier);
            text += "\u2502" + maxOfClass;
        }

        // When we know the text to display, we can determine the actual width needed for the glyph.
        final int lineHeight = VisConstants.getLineHeight();
        final int X_OFFSET = 6;
        final int Y_OFFSET = 2;
        final int maxWidth = 140; // pixels
        final CodeSparksGraphics graphics = getGraphics(maxWidth, lineHeight);
        final Font font = new Font("Arial", Font.BOLD, 11);
        graphics.setFont(font);

        final int textWidth = graphics.stringWidth(text);
        final int rectangleWidth = Math.max(maxWidth / 2, (int) (textWidth * 1.25f));
        final int totalWidth = rectangleWidth + 2 * X_OFFSET;

        /*
         * Draw the intensity rectangle
         */
        final Rectangle artifactVisualizationArea = new Rectangle(X_OFFSET, Y_OFFSET, rectangleWidth, lineHeight - Y_OFFSET);
        graphics.fillRectangle(artifactVisualizationArea, metricColor);

        /*
         * Draw the frame
         */
        final Rectangle frame = new Rectangle(X_OFFSET, Y_OFFSET, rectangleWidth - 1, lineHeight - Y_OFFSET - 1);
        graphics.drawRectangle(frame, BORDER_COLOR);

        /*
         * Draw the text
         */
        final Color textColor = VisualizationUtil.getTextColor(metricColor);
        final int textXPos = X_OFFSET / 2 + (int) ((totalWidth / 2d) - (textWidth / 2d)) - 1;
        final int textYPos = Y_OFFSET + (int) ((lineHeight - Y_OFFSET) * .75d);
        graphics.drawString(text, textXPos, textYPos, textColor);

        final JLabel jLabel = makeLabel(graphics, totalWidth);

        // Set custom cursor.
        final String resourceString = "/icons/question-256-flip-filled.png";
        final URL resource = getClass().getResource(resourceString);
        if (resource != null)
        {
            final ImageIcon imageIcon = new ImageIcon(resource);
            final int iconWidth = imageIcon.getIconWidth();
            final int iconHeight = imageIcon.getIconHeight();

            final Toolkit toolkit = jLabel.getToolkit();
            final Dimension bestCursorSize = toolkit.getBestCursorSize(iconWidth, iconHeight);
            final float scaleFactor = isLinux() ? .5f : 1f;
            final int imageWidth = Math.min(32, (int) (bestCursorSize.width * scaleFactor));
            final int imageHeight = Math.min(32, (int) (bestCursorSize.height * scaleFactor));
            final Image image = imageIcon.getImage();
            final Image scaledImage = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);

            final Cursor customCursor = toolkit.createCustomCursor(
                    scaledImage,
                    new Point(imageWidth / 2, imageHeight / 2),
                    "question-mark"
            );

            jLabel.setCursor(customCursor);
        } else
        {
            CodeSparksLogger.addText("Could not load resource: " + resourceString);
        }
        // Tooltip.
        jLabel.setToolTipText(toolTipText);

        return jLabel;
    }

    private boolean isLinux()
    {
        final String os = System.getProperty("os.name");
        return "Linux".equals(os);
    }
}
