/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.server.data.TaskSession;

import java.util.LinkedList;
import java.util.Queue;

public class SchedulerThread extends Thread {

    private static Integer workThreadCounter;
    private Integer workThreadThreshold;
    private Queue<TaskSession> taskSessionsQueue;

    private Queue<TaskSession> getTaskSessionsQueue() {
        return taskSessionsQueue;
    }

    public void setTaskSessionsQueue(Queue<TaskSession> taskSessionsQueue) {
        this.taskSessionsQueue = taskSessionsQueue;
    }

    public static void setWorkThreadCounter(Integer workThreadCounter) {
        synchronized (SchedulerThread.workThreadCounter) {
            SchedulerThread.workThreadCounter = workThreadCounter;
        }
    }

    public static Integer getWorkThreadCounter() {
        synchronized (SchedulerThread.workThreadCounter) {
            return workThreadCounter;
        }
    }

    public SchedulerThread(Integer workThreadThreshold) {
        SchedulerThread.workThreadCounter = 0;
        this.workThreadThreshold = workThreadThreshold;
        this.taskSessionsQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        while (true) {
            if (!taskSessionsQueue.isEmpty()) {
                if (SchedulerThread.getWorkThreadCounter() < workThreadThreshold) {
                    SchedulerThread.setWorkThreadCounter(SchedulerThread.getWorkThreadCounter() + 1);
                    WorkThread workThread = new WorkThread(taskSessionsQueue.remove());
                    workThread.start();
                }
            }
        }
    }
}