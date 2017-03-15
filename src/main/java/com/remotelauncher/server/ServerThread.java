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
import java.util.Set;

/**
 * Server thread runs a server, so the main thread will be free for UI frame
 */

public class ServerThread extends Thread {

    private Logger LOGGER;

    public ServerThread(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    private static ThreadGroup getRootThreadGroup(Thread thread) {
        ThreadGroup rootGroup = thread.getThreadGroup();
        while (rootGroup.getParent() != null) {
            rootGroup = rootGroup.getParent();
        }
        return rootGroup;
    }

    public WorkThread getWorkThread(long workThreadId){
        ThreadGroup rootThreadGroup = getRootThreadGroup(Thread.currentThread());
        Thread[] threads = new Thread[rootThreadGroup.activeCount()];
        rootThreadGroup.enumerate(threads);
        for (Thread tmp : threads) {
            if (tmp.getId() == workThreadId) {
                return (WorkThread) tmp;
            }
        }
        return null;
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
                WorkThread workThread = new WorkThread(clientSocket, token);
                if (!workThread.isDaemon()){
                    workThread.setDaemon(true);
                }
                if (clientData == null) {
                    long workThreadId = workThread.getId();
                    clientData = new ClientData(workThreadId);
                    LOGGER.info("CLIENT HAS NOT BEEN REGISTRED IN THE SYSTEM. HIS NEW WORK THREAD ID: {}", workThreadId);
                    ClientsDataTable.setData(userId, clientData);
                    workThread.start();
                }
                else {
                    long workThreadId = (long) clientData.getWorkThreadId();
                    LOGGER.info("CLIENT HAS ALREADY BEEN REGISTRED IN THE SYSTEM. HIS WORK THREAD ID: {} \n", workThreadId);
                    workThread = getWorkThread(workThreadId);
                    if (workThread != null) {
                        LOGGER.info("WE FOUND CLIENT THREAD");
                    }
                    else{
                        LOGGER.info("SOMETHING IS GOING WRONG...");
                    }

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
