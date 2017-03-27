/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.Constants;
import com.remotelauncher.server.data.TaskSession;

import java.util.LinkedList;
import java.util.Queue;

public class SchedulerThread extends Thread {

    private static Integer workThreadCounter;
    private final Integer workThreadThreshold = Constants.WORK_THREAD_THRESHOLD;
    private static Queue<TaskSession> taskSessionsQueue;

    public SchedulerThread() {
        workThreadCounter = 0;
        this.taskSessionsQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (taskSessionsQueue) {
                if (!taskSessionsQueue.isEmpty()) {
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

    private void saveTaskQueue(){
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