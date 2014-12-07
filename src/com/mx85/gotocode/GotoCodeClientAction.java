package com.mx85.gotocode;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;

public class GotoCodeClientAction extends AnAction {
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        Editor editor = event.getData(LangDataKeys.EDITOR);

        if(psiFile == null || editor == null) {
            event.getPresentation().setEnabled(false);
            return;
        }

        int lineCount = editor.getDocument().getLineCount();
        int lineNumber = editor.getDocument().getLineNumber(editor.getCaretModel().getOffset()) + 1;

        GotoCodeSendDialog gotoCodeSendDialog = new GotoCodeSendDialog(project, true, lineNumber, lineCount, ((PsiJavaFile)psiFile).getPackageName(), psiFile.getName());
        gotoCodeSendDialog.show();
    }
}
