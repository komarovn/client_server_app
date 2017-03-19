/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.Constants;
import com.remotelauncher.server.data.ClientData;
import com.remotelauncher.server.data.ClientsDataTable;
import com.remotelauncher.server.data.TaskSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
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

    /*
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
    */

    private void init() {
        SchedulerThread schedulerThread = new SchedulerThread(Constants.WORK_THREAD_THRESHOLD);
        {
            //TMP SECTION STARTED
            Queue<TaskSession> tmpQueue = new LinkedList<>();
            for (int i = 0; i < (Constants.WORK_THREAD_THRESHOLD * 4); i++) {
                tmpQueue.add(new TaskSession());
            }
            schedulerThread.setTaskSessionsQueue(tmpQueue);
            //TMP SECTION ENDED
        }
        schedulerThread.start();
        LOGGER.info("SERVER IS STARTING...");
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

    private String receiveToken(Socket clientSocket){
        String token = null;
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            token = dataInputStream.readUTF();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    private String receiveUserId(String token){
        String userId = Integer.toString(token.hashCode());
        return userId;
    }

    private void listenToClients(ServerSocket serverSocket){
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                String token = receiveToken(clientSocket);
                LOGGER.info("CLIENT WITH TOKEN {} WAS CONNECTED.", token);
                String userId = receiveUserId(token);
                ClientData clientData = ClientsDataTable.getData(userId);
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
