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
    private final Integer workThreadThreshold= Constants.WORK_THREAD_THRESHOLD;
    private static Queue<TaskSession> taskSessionsQueue;

    public SchedulerThread() {
        this.taskSessionsQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        while (true) {
            if (!taskSessionsQueue.isEmpty()) {
                if (SchedulerThread.getWorkThreadCounter() < workThreadThreshold) {
                    SchedulerThread.setWorkThreadCounter(SchedulerThread.getWorkThreadCounter() + 1);
                    WorkThread workThread = new WorkThread(SchedulerThread.getTaskSession());
                    workThread.start();
                }
            }
        }
    }

    public Queue<TaskSession> getTaskSessionsQueue() {
        return taskSessionsQueue;
    }

    public static void addTaskSession(TaskSession taskSession){
        synchronized (SchedulerThread.taskSessionsQueue) {
            taskSessionsQueue.add(taskSession);
        }
    }

    public static TaskSession getTaskSession(){
        synchronized (SchedulerThread.taskSessionsQueue){
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

}