/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.Constants;
import com.remotelauncher.server.data.*;
import com.remotelauncher.server.threads.communication.RequestThread;
import com.remotelauncher.server.threads.communication.ResponseThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Server thread runs a server, so the main thread will be free for UI frame
 */
public class ServerThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);
    private ServerSocket serverSocket = null;
    private List<CommunicationThread> communicationThreads = new ArrayList<>();
    SchedulerThread schedulerThread;

    @Override
    public void run() {
        init();
        listenToClients(serverSocket);
    }

    private void init() {
        LOGGER.info("SERVER IS STARTING...");
        schedulerThread = new SchedulerThread();
        schedulerThread.start();
        try {
            this.serverSocket = new ServerSocket(Constants.PORT_NUMBER);
            LOGGER.info("SERVER HAS STARTED.");
        } catch (IOException ex) {
            LOGGER.info("SERVER START HAS FAILED!");
            ex.printStackTrace();
            System.exit(0);
        }
        LOGGER.info("==== LISTENING FOR PORT {}...", Constants.PORT_NUMBER);
    }

    private void listenToClients(ServerSocket serverSocket) {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();

                /*
                RequestThread requestThread = new RequestThread(clientSocket);
                ResponseThread responseThread = new ResponseThread(clientSocket);
                requestThread.start();
                responseThread.start();
                */

                //TODO: Remove CommunicationThread and its starting after implementing message classification
                CommunicationThread communicationThread = new CommunicationThread(clientSocket);
                communicationThreads.add(communicationThread);
                communicationThread.start();

            } catch (IOException ex) {
                LOGGER.debug("Server socket is closed.");
            }
        }
    }

    public synchronized void stopServer() {
        for (Thread thread : communicationThreads) {
            LOGGER.debug("Communication thread {} is stopped.", thread.getId());
            ((CommunicationThread) thread).stopCommunicationThread();
        }
        schedulerThread.stopSchedulerThread();
        try {
            serverSocket.close();
            LOGGER.debug("Server thread is stopped.");
            stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}