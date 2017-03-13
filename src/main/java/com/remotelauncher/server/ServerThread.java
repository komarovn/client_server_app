/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

/**
 * Server thread runs a server, so the main thread will be free for doing something (I don't know why)
 */
public class ServerThread extends Thread {

    public synchronized void stopServer() {
        stop();
    }

}
