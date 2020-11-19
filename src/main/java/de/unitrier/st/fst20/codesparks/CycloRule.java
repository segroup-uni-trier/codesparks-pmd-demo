package de.unitrier.st.fst20.codesparks;

import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

public final class CycloRule extends AbstractJavaRule
{
    @Override
    public Object visit(final ASTConstructorDeclaration node, final Object data)
    {
        CodeSparksLogger.addText("Visited an ASTConstructorDeclaration");
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTMethodDeclaration node, final Object data)
    {
        final int cyclo = (int) MetricsUtil.computeMetric(JavaOperationMetricKey.CYCLO, node);

        final String name = node.getName();

        CodeSparksLogger.addText("Method " + name + " has cyclo value of " + cyclo);

        return super.visit(node, data);
    }
}
