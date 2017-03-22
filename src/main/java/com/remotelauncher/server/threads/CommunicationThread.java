/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Communication Thread is used for communicating with clients. It must receive request and send response only.
 */
public class CommunicationThread extends Thread {
    //TODO: add functionality of communication with clients
    private Logger LOGGER = LoggerFactory.getLogger(CommunicationThread.class);

    private Socket clientSocket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public CommunicationThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Send response.
     * @param response - sending response to client
     */
    public void sendResponse(Response response) {
        try {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
            LOGGER.debug("Client's address: {}: send response: {}", clientSocket.getInetAddress(), response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive request.
     * @return accepted request from client
     */
    public Request receiveRequest() {
        Request request = null;
        try {
            request = (Request) objectInputStream.readObject();
            LOGGER.debug("Client's address: {}, Received request: {}", clientSocket.getInetAddress(), request.toString());
        } catch (IOException|ClassNotFoundException e) {
            LOGGER.debug("Request is failed: client's address: {}", clientSocket.getInetAddress());
        }
        return request;
    }

    @Override
    public void run() {
        try {
            this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            while (clientSocket.isConnected()) {
                Request request = receiveRequest();
                Response response = new Response();
                if (request != null) {
                    processRequest(request, response);
                    sendResponse(response);
                }
            }
        } catch (IOException e) {
            LOGGER.info("Client is offline: client's address: {}", clientSocket.getInetAddress());
        }
    }

    //TODO: remove work with token to another class
    private void receiveToken(Request token, Response response) {
        try {
            String tok = (String) token.getParameter("token");
            //TODO: put user's token to DB.
            response.setParameter("message", "KOLYAN S PABHNHHbIX POLYAN");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String receiveUserId(String token) {
        String userId = Integer.toString(token.hashCode());
        return userId;
    }

    public void processRequest(Request request, Response response) {
        receiveToken(request, response);
    }
}
