/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.ServerConstants;
import com.remotelauncher.server.data.DatabaseOperations;
import com.remotelauncher.server.threads.communication.RequestThread;
import com.remotelauncher.server.threads.communication.ResponseThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server thread runs a server, so the main thread will be free for UI frame
 */
public class ServerThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);

    private ServerSocket serverSocket = null;
    private static List<Thread> communicationThreads = new ArrayList<>();
    private SchedulerThread schedulerThread;
    private static DatabaseOperations databaseOperations;

    @Override
    public void run() {
        init();
        databaseOperations = new DatabaseOperations();
        listenToClients(serverSocket);
    }

    private void init() {
        LOGGER.info("SERVER IS STARTING...");
        schedulerThread = new SchedulerThread();
        schedulerThread.start();
        try {
            this.serverSocket = new ServerSocket(ServerConstants.PORT_NUMBER);
            LOGGER.info("SERVER HAS STARTED.");
        } catch (IOException ex) {
            LOGGER.info("SERVER START HAS FAILED!");
            ex.printStackTrace();
            System.exit(0);
        }
        LOGGER.info("==== LISTENING FOR PORT {}...", ServerConstants.PORT_NUMBER);
    }

    private void listenToClients(ServerSocket serverSocket) {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                RequestThread requestThread = new RequestThread(clientSocket);
                ResponseThread responseThread = new ResponseThread(clientSocket);
                requestThread.addListener(responseThread);
                requestThread.start();
                responseThread.start();
                communicationThreads.add(responseThread);
                communicationThreads.add(requestThread);
            } catch (IOException ex) {
                LOGGER.debug("Server socket is closed.");
            }
        }
    }

    public synchronized void stopServer() {
        for (Thread thread : communicationThreads) {
            LOGGER.debug("Communication thread {} is stopped.", thread.getId());
            if (thread instanceof RequestThread) {
                ((RequestThread) thread).stopRequestThread();
            }
            if (thread instanceof ResponseThread) {
                ((ResponseThread) thread).stopResponseThread();
            }
        }
        schedulerThread.stopSchedulerThread();
        try {
            serverSocket.close();
            LOGGER.debug("Server thread is stopped.");
            databaseOperations.closeConnection();
            stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Thread> getCommunicationThreads() {
        return communicationThreads;
    }

    public static DatabaseOperations getDatabaseOperations() {
        return databaseOperations;
    }

}