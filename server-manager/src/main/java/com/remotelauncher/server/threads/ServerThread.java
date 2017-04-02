/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.ServerConstants;
import com.remotelauncher.server.threads.communication.RequestThread;
import com.remotelauncher.server.threads.communication.ResponseThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Server thread runs a server, so the main thread will be free for UI frame
 */
public class ServerThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(ServerThread.class);

    private ServerSocket serverSocket = null;
    private List<Thread> communicationThreads = new ArrayList<>();
    private SchedulerThread schedulerThread;
    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/remotelauncher?useSSL=false",
                    "root",
                    "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            closeConnection();
            stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static ResultSet execute(String query) {
        ResultSet result = null;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}