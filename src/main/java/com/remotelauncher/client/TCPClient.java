/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import com.remotelauncher.Constants;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class TCPClient {

    private Socket clientSocket;
    private Boolean isConnected;
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

    public Response processRequest(Request request) {
        try {
            if (clientSocket != null) {
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.writeObject(request);

                // send data to server
                outputStream.flush();

                // --- Server is processing request here ---
                if (!clientSocket.isClosed()) {
                    inputStream = new ObjectInputStream(clientSocket.getInputStream());
                }
                return (Response) inputStream.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processResponse(Response response) {

    }

    public Boolean getConnected() {
        return isConnected;
    }
}