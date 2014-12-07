package com.mx85.gotocode;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GotoCodeClient {
    public static final GotoCodeClient INSTANCE = new GotoCodeClient();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private  GotoCodeClient() {

    }

    public void send(String host, int port, int lineCount, int lineNumber, String packageName, String file) {
        ClientRunnable clientRunnable = new ClientRunnable(host, port, lineCount, lineNumber, packageName, file);
        executorService.execute(clientRunnable);
    }

    public static GotoCodeClient getInstance() {
        return INSTANCE;
    }

    private class ClientRunnable implements Runnable {

        private String host;
        private int port;
        private int lineCount;
        private int lineNumber;
        private String packageName;
        private String file;

        public ClientRunnable(String host, int port, int lineCount, int lineNumber, String packageName, String file) {
            this.host = host;
            this.port = port;
            this.lineCount = lineCount;
            this.lineNumber = lineNumber;
            this.packageName = packageName;
            this.file = file;
        }

        @Override
        public void run() {
            try {
                DatagramSocket datagramSocket = new DatagramSocket();
                String data = Integer.toString(lineCount) + "\r\n" + Integer.toString(lineNumber) + "\r\n" + packageName + "\r\n" + file + "\r\n\r\n";
                SocketAddress address = new InetSocketAddress(host, port);
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, address);
                datagramSocket.send(packet);
                datagramSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
