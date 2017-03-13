/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

public class ServerThread extends Thread {

    public synchronized void stopServer() {
        stop();
    }

}
