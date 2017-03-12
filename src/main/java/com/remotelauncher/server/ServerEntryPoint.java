/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

public class ServerEntryPoint {

    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer();
        tcpServer.runServer();
    }

}
