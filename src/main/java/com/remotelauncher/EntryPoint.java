/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

import com.remotelauncher.client.TCPClient;
import com.remotelauncher.server.TCPServer;

public class EntryPoint {

    public static void main(String[] args) {
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                TCPServer tcpServer = new TCPServer();
                tcpServer.runServer();
            }
        });
        Thread clientThread = new Thread (new Runnable() {
            @Override
            public void run() {
                TCPClient tcpClient = new TCPClient();
                tcpClient.runClient();
            }
        });
        serverThread.start();
        clientThread.start();
    }

}
