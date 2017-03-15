/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import com.remotelauncher.Constants;
import com.remotelauncher.client.gui.RemoteLauncher;
import javafx.application.Application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class TCPClient {

    private Boolean isConnected;

    public void runClient() {
        try {
            Socket clientSocket = null;
            try {
                clientSocket = new Socket(Constants.SERVER_NAME, Constants.PORT_NUMBER);
                isConnected = true;
                System.out.println("Connection established.");
            }
            catch (ConnectException e) {
                isConnected = false;
                System.out.println("Connection refused.");
            }
            Application.launch(RemoteLauncher.class, isConnected.toString());

            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            createRequest(outputStream);

            // send data to server
            outputStream.flush();

            // --- Server is processing request here ---

            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            processResponse(inputStream);

            outputStream.close();
            inputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRequest(DataOutputStream outputStream) {
        // TODO: create a some structure 'Request' for store request data
        // so, there will be method which encode object of 'Request' into output stream
        String token = "usersTokenopopo";
        try {
            outputStream.writeUTF(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processResponse(DataInputStream inputStream) {
        // TODO: create a some structure 'Response' for store response data, because we cannot always work with InputStream
        // so, there will be method which decode input stream into object of 'Response'
        try {
            System.out.printf(inputStream.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
