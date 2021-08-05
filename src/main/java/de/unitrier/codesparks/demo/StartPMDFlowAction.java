package de.unitrier.codesparks.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import org.jetbrains.annotations.NotNull;

public class StartPMDFlowAction extends AnAction
{
    @Override
    public void actionPerformed(@NotNull final AnActionEvent anActionEvent)
    {
        final Project project = anActionEvent.getProject();
        CodeSparksLogger.setup(project);
        final PMDFlow pmdFlow = new PMDFlow(project);
        new Thread(pmdFlow).start();
    }
}
