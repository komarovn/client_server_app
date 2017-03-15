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
    private Integer userId; // Should be a String
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public WorkThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            inputStream = new DataInputStream(clientSocket.getInputStream());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            String token = dataInputStream.readUTF();
            LOGGER.info("CLIENT WITH TOKEN {} WAS CONNECTED.", token);
            userId = token.hashCode();
            ClientData clientData = ClientsDataTable.getData(userId.toString());
            if (clientData == null) {
                long workThreadID = getId();
                clientData = new ClientData(workThreadID);
                LOGGER.info("CLIENT HAS NOT BEEN REGISTRED IN THE SYSTEM. HIS NEW WORK THREAD ID: {}", workThreadID);
                ClientsDataTable.setData(userId.toString(), clientData);
                String helloMessage = "Hello, I'm here to work with you " + userId;
                sendMessage(helloMessage);
            }
            else {
                long workThreadID = (long) clientData.getWorkThreadId();
                LOGGER.info("CLIENT HAS ALREADY BEEN REGISTRED IN THE SYSTEM. HIS WORK THREAD ID: {} \n", workThreadID);
                // TODO: send queue status to client
            }
        } catch (IOException e) {
            e.printStackTrace();
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
