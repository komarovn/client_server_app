/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.threads.communication;

import com.remotelauncher.client.listeners.RequestListener;
import com.remotelauncher.shared.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Thread for sending request to server.
 */
public class RequestThread extends Thread implements RequestListener {

    private Socket clientSocket;
    private ObjectOutputStream outputStream;

    public RequestThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            while (!clientSocket.isClosed()) {
                // waiting for request to send it.
            }
        } catch (IOException e) {
            System.out.print("Failed to create request thread");
        }
    }

    @Override
    public void sendRequest(Request request) {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                outputStream.writeObject(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
