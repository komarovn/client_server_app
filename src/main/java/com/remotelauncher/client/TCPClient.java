/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import com.remotelauncher.Constants;

import java.net.ConnectException;
import java.net.Socket;

public class TCPClient {

    private Socket clientSocket;
    private Boolean isConnected;

    public void runClient() {
        try {
            try {
                clientSocket = new Socket(Constants.SERVER_NAME, Constants.PORT_NUMBER);
                isConnected = true;
                System.out.println("Connection established.");
            } catch (ConnectException e) {
                isConnected = false;
                System.out.println("Connection refused.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public Boolean getConnected() {
        return isConnected;
    }
}