/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads.communication;

import com.remotelauncher.server.listeners.ResponseListener;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RequestThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(RequestThread.class);

    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private ResponseListener responseListener;

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
                processRequest(request);
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
        Request request = null;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                request = (Request) objectInputStream.readObject();
                LOGGER.debug("Client's address: {}, Received request: {}", clientSocket.getInetAddress(), request.toString());
            }
        } catch (IOException|ClassNotFoundException e) {
            LOGGER.debug("Request is failed: client's address: {}", clientSocket.getInetAddress());
        }
        return request;
    }

    public void addListener(ResponseListener responseListener){
        this.responseListener = responseListener;
    }

    private void receiveToken(Request token) {
        try {
            String tok = (String) token.getParameter("token");
            //TODO: put user's token to DB.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String receiveUserId(String token) {
        String userId = Integer.toString(token.hashCode());
        return userId;
    }

    public void processRequest(Request request) {
        Response response = new Response();
        receiveToken(request);
        response.setParameter("message", "WELL CUM, PSINA!");
        responseListener.sendResponse(response);
    }

    public void stopRequestThread(){
        stop();
    }

}
