/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client (work) thread operates with one exact client. For N clients there will be N client threads. It provides
 * processing of the request and creating data for response.
 */
public class WorkThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(WorkThread.class);
    private Socket clientSocket;
    private String userId;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public WorkThread(Socket clientSocket, String userId) {
        this.clientSocket = clientSocket;
        this.userId = userId;
        try {
            outputStream = new DataOutputStream(this.clientSocket.getOutputStream());
            inputStream = new DataInputStream(this.clientSocket.getInputStream());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String wellcomeMessage = "Hello, I'm here to work with you " + userId;
        sendMessage(wellcomeMessage);
        while (true) {
            try {
                sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
            outputStream.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
