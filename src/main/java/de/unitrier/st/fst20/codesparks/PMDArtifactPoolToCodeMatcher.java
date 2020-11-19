package de.unitrier.st.fst20.codesparks;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.IArtifactPoolToCodeMatcher;
import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.ArrayList;
import java.util.Collection;

public class PMDArtifactPoolToCodeMatcher implements IArtifactPoolToCodeMatcher
{
    @Override
    public Collection<AArtifact> matchArtifactsToCodeFiles(final IArtifactPool artifactPool, final Project project, final VirtualFile... files)
    {
        Collection<AArtifact> matchedArtifacts = new ArrayList<>();
        if (artifactPool == null)
        {
            return matchedArtifacts;
        }

        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);

        for (VirtualFile file : files)
        {
            final String canonicalPath = file.getCanonicalPath();
            assert canonicalPath != null;

            final String fileName = canonicalPath.replace('/', '\\');

            PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
                PsiManager psiManager = PsiManager.getInstance(project);
                return psiManager.findFile(file);
            });

            final Document document = documentManager.getDocument(psiFile);
            assert document != null;

            final Collection<PsiMethod> psiMethods = ApplicationManager.getApplication().runReadAction((Computable<Collection<PsiMethod>>) () ->
                    PsiTreeUtil.findChildrenOfType(psiFile, PsiMethod.class));

            for (final PsiMethod psiMethod : psiMethods)
            {
                final String name = ApplicationManager.getApplication().runReadAction((Computable<String>) psiMethod::getName);

                final int lineNumber =
                        ApplicationManager.getApplication().runReadAction((Computable<Integer>) () -> document.getLineNumber(psiMethod.getTextOffset()));
                final int lineEndOffset = document.getLineEndOffset(lineNumber);
                final int lineNumberOfLineEndOffset = document.getLineNumber(lineEndOffset + 1);

                final String artifactIdentifier = PMDArtifactUtil.getArtifactIdentifier(fileName, name, lineNumberOfLineEndOffset);

//                System.out.println(artifactIdentifier);

                final AArtifact artifact = artifactPool.getArtifact(artifactIdentifier);

                if (artifact != null)
                {
                    PsiParameterList parameterList =
                            ApplicationManager.getApplication().runReadAction((Computable<PsiParameterList>) psiMethod::getParameterList);
                    artifact.setVisPsiElement(parameterList);
                    matchedArtifacts.add(artifact);
                }
            }
        }
        return matchedArtifacts;
    }
}
