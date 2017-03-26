/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import com.remotelauncher.shared.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
                // waiting for request.
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
