/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Communication with server.
 */
public class CommunicationThread extends Thread {

    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public CommunicationThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            while (clientSocket.isConnected()) {
                //TODO: listen for responses.
            }
        } catch (IOException e) {
            System.out.print("Failed to create comunication thread");
        }
    }

    public Response processRequest(Request request) {
        try {
            if (clientSocket != null) {
                outputStream.writeObject(request);

                // send data to server
                //outputStream.flush();

                // --- Server is processing request here ---
                if (!clientSocket.isClosed()) {
                    return (Response) inputStream.readObject();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void processResponse(Response response) {

    }
}
