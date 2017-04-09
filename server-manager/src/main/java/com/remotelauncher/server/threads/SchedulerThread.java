/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.ServerConstants;
import com.remotelauncher.server.data.TaskSession;
import com.remotelauncher.server.threads.communication.ResponseThread;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Response;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SchedulerThread extends Thread {

    private static Integer workThreadCounter;
    private final Integer workThreadThreshold = ServerConstants.WORK_THREAD_THRESHOLD;
    private static Queue<TaskSession> taskSessionsQueue;

    public SchedulerThread() {
        workThreadCounter = 0;
        this.taskSessionsQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (taskSessionsQueue) {
                if (!taskSessionsQueue.isEmpty()) {
                    //TODO: set on execution all available tasksessions after sleep
                    if (SchedulerThread.getWorkThreadCounter() < workThreadThreshold) {
                        SchedulerThread.setWorkThreadCounter(SchedulerThread.getWorkThreadCounter() + 1);
                        WorkThread workThread = new WorkThread(SchedulerThread.getTaskSession());
                        workThread.start();
                    }
                }
            }
        }
    }

    public static void addTaskSession(TaskSession taskSession) {
        synchronized (SchedulerThread.taskSessionsQueue) {
            taskSessionsQueue.add(taskSession);
        }
        WorkThread.sendUpdateOfTaskSessionQueue("TASK INSERT QUEUE UPDATE\n");
    }

    public static TaskSession getTaskSession() {
        synchronized (SchedulerThread.taskSessionsQueue) {
            return taskSessionsQueue.remove();
        }
    }

    public static void setWorkThreadCounter(Integer workThreadCounter) {
        SchedulerThread.workThreadCounter = workThreadCounter;
    }

    public static Integer getWorkThreadCounter() {
        synchronized (SchedulerThread.workThreadCounter) {
            return workThreadCounter;
        }
    }

    private void saveTaskQueue() {
        //Save current taskQueue to DB
    }

    public void stopSchedulerThread() {
        while (SchedulerThread.getWorkThreadCounter() != 0) {
            //Here we are waiting until all the running workthreads finish their task sessions
            //Kind of soft stopping
        }
        saveTaskQueue();
        stop();
    }

}