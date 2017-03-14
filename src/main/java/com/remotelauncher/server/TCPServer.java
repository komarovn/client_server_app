/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private Logger LOGGER = LoggerFactory.getLogger(TCPServer.class);

    public void runServer() {
        ServerSocket serverSocket = null;
        LOGGER.info("SERVER IS STARTING...");
        try {
            serverSocket = new ServerSocket(Constants.PORT_NUMBER);
            LOGGER.info("SERVER HAS STARTED.");
        } catch (IOException ex) {
            LOGGER.info("SERVER START HAS FAILED!");
            ex.printStackTrace();
            System.exit(0);
        }
        LOGGER.info("==== LISTENING FOR PORT {}...", Constants.PORT_NUMBER);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                WorkThread connection = new WorkThread(clientSocket);
                connection.run();
                LOGGER.info("---- LOOKING FOR NEW CLIENTS...");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
