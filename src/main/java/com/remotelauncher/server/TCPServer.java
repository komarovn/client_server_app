/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    public void runServer() {
        ServerSocket serverSocket = null;
        System.out.println("SERVER IS STARTING...");
        try {
            serverSocket = new ServerSocket(Constants.PORT_NUMBER);
            System.out.println("SERVER HAS STARTED.");
        } catch (IOException ex) {
            System.out.println("SERVER START HAS FAILED!");
            ex.printStackTrace();
            System.exit(0);
        }
        System.out.printf("====\nLISTENING FOR PORT %d...\n", Constants.PORT_NUMBER);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientThread connection = new ClientThread(clientSocket);
                connection.run();
                System.out.println("----\nLOOKING FOR NEW CLIENTS...");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void processResponse() {

    }

}
