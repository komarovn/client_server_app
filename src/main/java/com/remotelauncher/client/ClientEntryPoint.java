/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

public class ClientEntryPoint {

    public static void main(String[] args) {
        TCPClient tcpClient = new TCPClient();
        tcpClient.runClient();
    }

}
