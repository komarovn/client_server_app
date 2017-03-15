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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server thread runs a server, so the main thread will be free for doing something (I don't know why)
 */
public class ServerThread extends Thread {

    private Logger LOGGER;

    public ServerThread(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    @Override
    public void run() {
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


    public synchronized void stopServer() {
        Thread[] threads = new Thread[currentThread().getThreadGroup().activeCount()];
        currentThread().getThreadGroup().enumerate(threads);
        for (Thread thread : threads) {
            thread.stop();
        }
        stop();
    }

}
