/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import java.net.Socket;

/**
 * Client thread operates with one exact client. For N clients there will be N client threads. It provides processing
 * of the request and creating data for response.
 */
public class ClientThread extends Thread {
    // Probably WorkThread should be transferred here

    public ClientThread(Socket clientSocket) {

    }

}
