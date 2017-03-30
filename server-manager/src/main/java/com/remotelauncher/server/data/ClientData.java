/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.data;

public class ClientData {

    private long workThreadId;

    public ClientData(long workThreadId) {
        this.workThreadId = workThreadId;
    }

    public long getWorkThreadId() {
        return workThreadId;
    }

    public void setWorkThreadId(long workThreadId) {
        this.workThreadId = workThreadId;
    }

}