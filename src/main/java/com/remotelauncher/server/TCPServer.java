/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private Logger LOGGER = LoggerFactory.getLogger(TCPServer.class);

    public void runServer() {
        //TODO: Add server side UI frame in main thread.
        //TODO: Via button START SERVER on UI will be created and started ServerThread
        //TODO: Pass via UI port to listen and edit ServerThread constructor and fields to store the port
        //TODO: Call server.stopServer() from the UI by clicking 'STOP SERVER'
        ServerThread server = new ServerThread(LOGGER);
        server.start();
    }

}
