/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class WorkThread extends Thread {

    private ClientsDataTable clientsDataTable;
    private Socket clientSocket;
    private String userId;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public WorkThread(ClientsDataTable clientsDataTable, String userId, Socket socket) {
        this.clientsDataTable = clientsDataTable;
        this.clientSocket = socket;
        this.userId = userId;

        try {
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            inputStream = new DataInputStream(clientSocket.getInputStream());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendMessage(String message){
        try {
            outputStream.writeUTF(message);
            outputStream.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        String helloMessage = "Hello, I'm here to work with you " + userId;
        sendMessage(helloMessage);
    }

}
