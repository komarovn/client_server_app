/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads.communication;

import com.remotelauncher.server.threads.CommunicationThread;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RequestThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(CommunicationThread.class);

    private Socket clientSocket;
    private ObjectInputStream objectInputStream;

    public RequestThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!clientSocket.isClosed()) {
            final Request request = receiveRequest();
            if (request != null) {
                //TODO: Parse requests
            } else {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stop();
            }
        }
    }

    public Request receiveRequest() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                if (!clientSocket.isClosed()) {
                    return (Request) objectInputStream.readObject();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
