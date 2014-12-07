package com.mx85.gotocode;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GotoCodeSendDialog extends DialogWrapper {

    private int lineCount = 0;
    private int lineNumber = 0;
    private String packageName = null;
    private String file = null;
    private JTextField sentToTextField;

    protected GotoCodeSendDialog(@Nullable Project project, boolean canBeParent, int lineCount,  int lineNumber, String packageName, String file) {
        super(project, canBeParent);
        this.lineCount = lineCount;
        this.lineNumber = lineNumber;
        this.packageName = packageName;
        this.file = file;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel sentToLabel = new JLabel();
        sentToLabel.setText("Send to: ");
        sentToTextField = new JTextField();
        sentToTextField.setColumns(10);
        JButton sendButton = new JButton();
        sendButton.setText("Send");
        sendButton.addActionListener(new SendActionListener());

        panel.add(sentToLabel);
        panel.add(sentToTextField);
        panel.add(sendButton);

        return panel;
    }

    private class SendActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String hostAndPort = sentToTextField.getText();
            String[] splits = hostAndPort.split(":");
            String host = splits[0];
            int port = 11234;
            if(splits.length > 1) {
                try {
                    port = Integer.parseInt(splits[1]);
                } catch (NumberFormatException ex) {
                    port = 11234;
                }
            }
            if(host != null && !host.equals("")) {
                GotoCodeClient.getInstance().send(host, port, lineCount, lineNumber, packageName, file);
            }
        }
    }
}
