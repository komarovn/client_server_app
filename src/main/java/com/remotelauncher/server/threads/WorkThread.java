/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.server.data.TaskSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WorkThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(WorkThread.class);
    private TaskSession taskSession;

    public WorkThread(TaskSession taskSession) {
        this.taskSession = taskSession;
    }

    @Override
    public void run() {
        //TODO: Execute task session
        LOGGER.info("WORKTHREAD {} IS STARTED! {}", this.getId(), SchedulerThread.getWorkThreadCounter());
        execute();
        SchedulerThread.setWorkThreadCounter(SchedulerThread.getWorkThreadCounter() - 1);
    }

    private void execute() {
        Process execution = null;
        Runtime runtime = Runtime.getRuntime();
        try {
            execution = runtime.exec(taskSession.getTask());
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("{}'s TASK COMPLETED", taskSession.getAuthor());
        if (execution.getOutputStream() != null) {
            LOGGER.info("THAT'S STDPUT: {}", execution.getOutputStream());
        }
        if (execution.getErrorStream() != null) {
            LOGGER.info("THAT'S STDERR: {}", execution.getErrorStream());
        }
    }

}