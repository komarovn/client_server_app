/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.Constants;
import com.remotelauncher.server.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Server thread runs a server, so the main thread will be free for UI frame
 */

public class ServerThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);
    private ServerSocket serverSocket = null;

    private void init() {
        LOGGER.info("SERVER IS STARTING...");
        SchedulerThread schedulerThread = new SchedulerThread(Constants.WORK_THREAD_THRESHOLD);
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
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                CommunicationThread communicationThread = new CommunicationThread(clientSocket);
                communicationThread.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        init();
        listenToClients(serverSocket);
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