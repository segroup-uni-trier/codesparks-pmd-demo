package de.unitrier.codesparks.demo;

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
import java.util.List;

final class PMDArtifactPoolToCodeMatcher implements IArtifactPoolToCodeMatcher
{
    @Override
    public Collection<AArtifact> matchArtifactsToCodeFiles(final IArtifactPool artifactPool, final Project project, final VirtualFile... files)
    {
        final Collection<AArtifact> matchedArtifacts = new ArrayList<>();
        if (artifactPool == null)
        {
            return matchedArtifacts;
        }

        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);

        for (final VirtualFile file : files)
        {
            final String canonicalPath = file.getCanonicalPath();
            assert canonicalPath != null;

            final String fileName = canonicalPath.replace('/', '\\');

            final PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
                final PsiManager psiManager = PsiManager.getInstance(project);
                return psiManager.findFile(file);
            });

            final Document document = documentManager.getDocument(psiFile);
            assert document != null;

            final List<PsiNameIdentifierOwner> psiElements =
                    new ArrayList<>(ApplicationManager.getApplication().runReadAction((Computable<Collection<PsiMethod>>) () ->
                            PsiTreeUtil.findChildrenOfType(psiFile, PsiMethod.class)));

            psiElements.addAll(ApplicationManager.getApplication().runReadAction((Computable<Collection<PsiClass>>) () ->
                    PsiTreeUtil.findChildrenOfType(psiFile, PsiClass.class)));

            for (final PsiNameIdentifierOwner psiElement : psiElements)
            {
                final String name = ApplicationManager.getApplication().runReadAction((Computable<String>) psiElement::getName);

                final int lineNumber =
                        ApplicationManager.getApplication().runReadAction((Computable<Integer>) () -> document.getLineNumber(psiElement.getTextOffset()));
                final int lineEndOffset = document.getLineEndOffset(lineNumber);
                final int lineNumberOfLineEndOffset = document.getLineNumber(lineEndOffset + 1);

                final String artifactIdentifier = PMDArtifactUtil.getArtifactIdentifier(fileName, name, lineNumberOfLineEndOffset);

                final AArtifact artifact = artifactPool.getArtifact(artifactIdentifier);

                if (artifact == null)
                {
                    continue;
                }

                if (psiElement instanceof PsiMethod)
                {
                    final PsiMethod psiMethod = (PsiMethod) psiElement;
                    final PsiParameterList parameterList =
                            ApplicationManager.getApplication().runReadAction((Computable<PsiParameterList>) psiMethod::getParameterList);
                    artifact.setVisPsiElement(parameterList);
                    matchedArtifacts.add(artifact);
                } else
                {
                    if (psiElement instanceof PsiClass)
                    {
                        final PsiClass psiClass = (PsiClass) psiElement;
                        final PsiReferenceList referenceList =
                                ApplicationManager.getApplication().runReadAction((Computable<PsiReferenceList>) psiClass::getImplementsList);
                        artifact.setVisPsiElement(referenceList);
                        matchedArtifacts.add(artifact);
                    }
                }
            }
        }
        return matchedArtifacts;
    }
}
