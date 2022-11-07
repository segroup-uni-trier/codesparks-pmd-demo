package de.unitrier.codesparks.demo;

import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.visualization.*;
import de.unitrier.st.codesparks.java.JavaUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static de.unitrier.st.codesparks.core.visualization.VisConstants.BORDER_COLOR;

final class CycloArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    CycloArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    // https://colorbrewer2.org/#type=sequential&scheme=PuRd&n=4
    private static final Color SIMPLE_COLOR = Color.decode("#feebe2");
    private static final Color MORE_COMPLEX_COLOR = Color.decode("#fbb4b9");
    private static final Color COMPLEX_COLOR = Color.decode("#f768a1");
    private static final Color UNTESTABLE_COLOR = Color.decode("#ae017e");

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

        final boolean isMethodArtifact = JavaUtil.isMethodArtifact(artifact);

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

//            final String scope = "with respect to artifacts of the same level of abstraction in the project";

            final String explanation = "sum of methods";
            final String scope = "compared to other classes in the project";
            double lowThreshold = cycloMean - cycloSD;
            double highThreshold = cycloMean + cycloMean;
            double veryHighThreshold = (cycloMean + cycloMean) * 1.5;

            final String stat = "mean=" +
                    CoreUtil.roundAndFormatToDigitsAfterComma(cycloMean, 2) +
                    ", sd=" +
                    CoreUtil.roundAndFormatToDigitsAfterComma(cycloSD, 2);

            final String interpretation;
            if (metricValue < lowThreshold)
            {
                metricColor = SIMPLE_COLOR;
                interpretation = "Low";
//                toolTipText += "Less than " + CoreUtil.formatToDigitsAfterComma(lowThreshold, 2) + " (mean-sd";
            } else
            {
                if (metricValue < highThreshold)
                {
                    metricColor = MORE_COMPLEX_COLOR;
//                    toolTipText += "Less than " + CoreUtil.formatToDigitsAfterComma(highThreshold, 2) + " (mean+sd";
                    interpretation = "Average";
                } else
                {
                    if (metricValue < veryHighThreshold)
                    {
                        metricColor = COMPLEX_COLOR;
//                        toolTipText += "Less than " + CoreUtil.formatToDigitsAfterComma(veryHighThreshold, 2) + " ((mean+sd)*1.5";
                        interpretation = "High";
                    } else
                    {
                        metricColor = UNTESTABLE_COLOR;
//                        toolTipText += "Higher than " + CoreUtil.formatToDigitsAfterComma(veryHighThreshold, 2) + " ((mean+sd)*1.5";
                        interpretation = "Very high";
                    }
                }
            }
            toolTipText = String.format("Cyclomatic Complexity: %d (%s) >> %s %s.", metricValue, explanation, interpretation, scope);
        }
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

        final JLabel jLabel = makeLabel(graphics);
        final Toolkit toolkit = jLabel.getToolkit();
        //jLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        final URL resource = getClass().getResource("/icons/question-mark.png");
//        final String resourceString = "/icons/question-mark-512.png";
        final String resourceString = "/icons/question-256-flip-filled.png";
        final URL resource = getClass().getResource(resourceString);
        if (resource != null)
        {
            final ImageIcon imageIcon = new ImageIcon(resource);
            final int iconWidth = imageIcon.getIconWidth();
            final int iconHeight = imageIcon.getIconHeight();
            final Dimension bestCursorSize = toolkit.getBestCursorSize(iconWidth, iconHeight);
            final Cursor customCursor = toolkit.createCustomCursor(
                    imageIcon.getImage(),
                    new Point(bestCursorSize.width / 2, bestCursorSize.height / 2),
                    "question-mark"
            );
            jLabel.setCursor(customCursor);
        } else
        {
            CodeSparksLogger.addText("Could not load resource: " + resourceString);
        }
        jLabel.setToolTipText(toolTipText);

        return jLabel;
    }
}
