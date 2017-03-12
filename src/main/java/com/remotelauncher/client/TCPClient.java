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
import java.net.Socket;

public class TCPClient {

    public void runClient() {
        try {
            Socket clientSocket = new Socket(Constants.SERVER_NAME, Constants.PORT_NUMBER);
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());

            String token = "usersToken";
            outputStream.writeUTF(token);
            outputStream.flush();
            System.out.println("Connection was established.");

            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            System.out.printf(inputStream.readUTF());

            Application.launch(RemoteLauncher.class);

            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
