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

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server thread runs a server, so the main thread will be free for UI frame
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
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                String token = dataInputStream.readUTF();
                LOGGER.info("CLIENT WITH TOKEN {} WAS CONNECTED.", token);
                String userId = Integer.toString(token.hashCode());
                ClientData clientData = ClientsDataTable.getData(userId);
                if (clientData == null) {
                    WorkThread workThread = new WorkThread(clientSocket, userId);
                    long workThreadID = workThread.getId();
                    clientData = new ClientData(workThreadID);
                    LOGGER.info("CLIENT HAS NOT BEEN REGISTRED IN THE SYSTEM. HIS NEW WORK THREAD ID: {}", workThreadID);
                    ClientsDataTable.setData(userId, clientData);
                    workThread.run();
                }
                else {
                    long workThreadID = (long) clientData.getWorkThreadId();
                    LOGGER.info("CLIENT HAS ALREADY BEEN REGISTRED IN THE SYSTEM. HIS WORK THREAD ID: {} \n", workThreadID);
                    // TODO: send queue status to client
                }
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
