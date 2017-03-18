/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.Constants;
import org.slf4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

/**
 * Server thread runs a server, so the main thread will be free for UI frame
 */

public class ServerThread extends Thread {

    private Logger LOGGER;
    private ServerSocket serverSocket = null;
    public Queue<TaskSession> taskSessionsQueue;


    public ServerThread(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

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
    
    private void preparation(){
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

    private void mainLoop(ServerSocket serverSocket){
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                String token = receiveToken(clientSocket);
                LOGGER.info("CLIENT WITH TOKEN {} WAS CONNECTED.", token);
                String userId = receiveUserId(token);
                ClientData clientData = ClientsDataTable.getData(userId);

                /*
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
                */
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        this.preparation();
        this.mainLoop(serverSocket);
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
