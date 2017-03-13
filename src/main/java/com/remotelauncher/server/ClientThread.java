/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client thread operates with one exact client. For N clients there will be N client threads. It provides processing
 * of the request and creating data for response.
 */
public class ClientThread extends Thread {
    // Probably WorkThread should be transferred here

    private Socket clientSocket;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            String token = dataInputStream.readUTF();
            System.out.printf("CLIENT WITH TOKEN %s WAS CONNECTED.\n", token);
            Integer userId = token.hashCode();
            ClientData clientData = ClientsDataTable.getData(userId.toString());
            if (clientData == null) {
                WorkThread workThread = new WorkThread(ClientsDataTable.singleton, userId.toString(), clientSocket);
                workThread.run();
                long workThreadID = workThread.getId();
                clientData = new ClientData(workThreadID);
                System.out.printf("CLIENT HAS NOT BEEN REGISTRED IN THE SYSTEM. HIS NEW WORK THREAD ID: %s \n", workThreadID);
                ClientsDataTable.setData(userId.toString(), clientData);
            }
            else {
                long workThreadID = (long) clientData.getWorkThreadId();
                System.out.printf("CLIENT HAS ALREADY BEEN REGISTRED IN THE SYSTEM. HIS NEW WORK THREAD ID: %s \n", workThreadID);
                // TODO: send queue status to client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
