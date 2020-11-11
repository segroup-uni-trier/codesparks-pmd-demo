package de.unitrier.st.codesparks.fst20.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.fst20.PMDFlow;
import org.jetbrains.annotations.NotNull;

public class StartPMDFlow extends AnAction
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
