/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client (work) thread operates with one exact client. For N clients there will be N client threads. It provides
 * processing of the request and creating data for response.
 */
public class WorkThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(WorkThread.class);
    private TaskSession taskSession;


    public WorkThread(TaskSession taskSession) {
        this.taskSession = taskSession;
    }


    @Override
    public void run() {
        //TODO: Execute task session
        LOGGER.info("WORKTHREAD {} IS STARTED, P'IOS! {}", this.getId(), SchedulerThread.getWorkThreadCounter());
        try {
            sleep(getId()*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SchedulerThread.setWorkThreadCounter(SchedulerThread.getWorkThreadCounter() - 1);
        LOGGER.info("WORKTHREAD {} HAS DONE A TASK, P'IOS! {}", this.getId(), SchedulerThread.getWorkThreadCounter());
    }

}
