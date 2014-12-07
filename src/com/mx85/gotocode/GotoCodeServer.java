package com.mx85.gotocode;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.PackageIndex;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GotoCodeServer {

    public static final GotoCodeServer INSTANCE = new GotoCodeServer();
    private ServerRunnable serverRunnable;
    private ExecutorService executorService;
    private volatile DatagramSocket datagramSocket;
    private int port = 11234;
    private Project project;

    private GotoCodeServer() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public static GotoCodeServer getInstance() {
        return INSTANCE;
    }

    public boolean startServer(int port, Project project) {
        this.project = project;
        if(serverRunnable != null && serverRunnable.isRunning()) {
            // Server is already running
            return false;
        }
        try {
            datagramSocket = new DatagramSocket(port);
            this.port = port;
            serverRunnable = new ServerRunnable();
            executorService.execute(serverRunnable);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopServer() {
        if(serverRunnable == null) {
            // Server is not started
            return;
        }
        serverRunnable.stop();
    }

    public boolean isRunning() {
        if(serverRunnable == null){
            return false;
        }
        return serverRunnable.isRunning();
    }

    public int getPort() {
        return port;
    }

    private class ServerRunnable implements Runnable {
        private boolean isRunning = true;
        @Override
        public void run() {
            while (isRunning) {
                try {
                    byte[] data = new byte[1024];
                    DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                    datagramSocket.receive(datagramPacket);
                    data = datagramPacket.getData();
                    String text = new String(data);
                    final String[] splits = text.split("\r\n");

                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            VirtualFile[] files = PackageIndex.getInstance(project).getDirectoriesByPackageName(splits[2], true);
                            for(VirtualFile f: files) {
                                if(f.isDirectory()) {
                                    VirtualFile vf = f.findChild(splits[3]);
                                    FileEditorManager.getInstance(project).openFile(vf, true);
                                }
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

                stop();
            }
        }

        public synchronized void stop() {
            datagramSocket.close();
            isRunning = false;

        }

        public synchronized boolean isRunning() {
            return isRunning;
        }
    }
}
