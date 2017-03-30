/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

import com.remotelauncher.server.threads.ServerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPServer {

    private Logger LOGGER = LoggerFactory.getLogger(TCPServer.class);

    private ServerThread server;

    public void runServer() {
        //TODO: Pass via UI port to listen and edit ServerThread constructor and fields to store the port
        server = new ServerThread();
        server.start();
    }

    public void stopServer() {
        if (server != null && server.isAlive()) {
            server.stopServer();
        }
    }

}