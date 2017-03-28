/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads.communication;

import com.remotelauncher.server.listeners.ResponseListener;
import com.remotelauncher.server.threads.CommunicationThread;
import com.remotelauncher.shared.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ResponseThread extends Thread implements ResponseListener {

    private Logger LOGGER = LoggerFactory.getLogger(CommunicationThread.class);

    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;

    public ResponseThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            objectOutputStream = (ObjectOutputStream) clientSocket.getOutputStream();
            while (!clientSocket.isClosed()) {
                try {
                    sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // waiting for response to send it.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendResponse(Response response) {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                objectOutputStream.writeObject(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopCommunication() {
        Response response = new Response();
        response.setParameter("serverState", "STOPPED");
        if (!clientSocket.isClosed()) {
            sendResponse(response);
        }
        stop();
    }

}
