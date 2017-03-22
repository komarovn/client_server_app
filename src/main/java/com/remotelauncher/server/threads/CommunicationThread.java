/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.server.data.ClientData;
import com.remotelauncher.server.data.ClientsDataTable;
import com.remotelauncher.server.data.Request;
import com.remotelauncher.server.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommunicationThread extends Thread {
    //TODO: add functionality of communication with clients
    private Logger LOGGER = LoggerFactory.getLogger(CommunicationThread.class);

    private Socket clientSocket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public CommunicationThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            //this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            //this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            String token = receiveToken(clientSocket);
            LOGGER.info("CLIENT WITH TOKEN {} WAS CONNECTED.", token);
            String userId = receiveUserId(token);
            ClientData clientData = ClientsDataTable.getData(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendResponse(Response response) {
        try {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
            LOGGER.debug("Client id: send response: {}", response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Request receiveRequest() {
        Request request = null;
        try {
            request = (Request) objectInputStream.readObject();
            LOGGER.debug("Client id: received request: {}", request.toString());
        } catch (IOException e) {
            try {
                clientSocket.shutdownInput();
                clientSocket.shutdownOutput();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return request;
    }

    @Override
    public void run() {
        while (clientSocket.isConnected()) {
            Request request = receiveRequest();
            //TODO: Process request
            sendResponse(new Response());
        }
    }

    private String receiveToken(Socket clientSocket) {
        Request token = null;
        try {
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            token = (Request) objectInputStream.readObject();
            Response response = new Response();
            response.setParameter("message", "KOLYAN S PABHNHHbIX POLYAN");
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String) token.getParameter("token");
    }

    private String receiveUserId(String token) {
        String userId = Integer.toString(token.hashCode());
        return userId;
    }
}
