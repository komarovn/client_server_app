/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.server.data.Request;
import com.remotelauncher.server.data.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommunicationThread extends Thread {
    //TODO: add functionality of communication with clients
    private Socket clientSocket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public CommunicationThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendResponse(Response response) {
        try {
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveRequest() {
        Request request;
        try {
            request = (Request) objectInputStream.readObject();
            //TODO: Process request
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
    }

    @Override
    public void run() {
        while (clientSocket.isConnected()) {
            receiveRequest();
        }
    }
}
