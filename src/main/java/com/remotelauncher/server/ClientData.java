/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

public class ClientData extends Object {

    private long workThreadId;

    public ClientData(long _workThreadId) {
        workThreadId = _workThreadId;
    }
    public long getWorkThreadId(){
        return workThreadId;
    }
    public void setWorkThreadId(long _workThreadId) {
        workThreadId = _workThreadId;
        return;
    }
}
