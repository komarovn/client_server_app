/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

import com.remotelauncher.server.TCPServer;

public class EntryPoint {

    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer();
        tcpServer.runServer();
    }

}
