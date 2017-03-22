/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import com.remotelauncher.Constants;
import com.remotelauncher.server.data.Request;
import com.remotelauncher.server.data.Response;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class TCPClient {

    private Socket clientSocket;
    private Boolean isConnected;
    private String token;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public void runClient() {
        try {
            try {
                clientSocket = new Socket(Constants.SERVER_NAME, Constants.PORT_NUMBER);
                isConnected = true;
                System.out.println("Connection established.");
            } catch (ConnectException e) {
                isConnected = false;
                System.out.println("Connection refused.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process() {
        try {
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            createRequest(outputStream);

            // send data to server
            outputStream.flush();

            // --- Server is processing request here ---
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            processResponse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                clientSocket.shutdownOutput();
                clientSocket.shutdownInput();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void createRequest(ObjectOutputStream outputStream) {
        try {
            if (token != null) {
                Request request = new Request();
                request.setParameter("token", token);
                outputStream.writeObject(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processResponse(ObjectInputStream inputStream) {
        try {
            Response response = (Response) inputStream.readObject();
            if (response.getParameter("message") != null) {
                System.out.printf((String) response.getParameter("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getConnected() {
        return isConnected;
    }

    public void setToken(String token) {
        this.token = token;
    }
}