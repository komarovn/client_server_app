/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.ServerConstants;
import com.remotelauncher.server.data.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SchedulerThread extends Thread {

    private static Integer workThreadCounter;
    private final Integer workThreadThreshold = ServerConstants.WORK_THREAD_THRESHOLD;
    private static Queue<List<Task>> taskSessionsQueue;

    public SchedulerThread() {
        // TODO: get all uncomleted tasks from db.
        workThreadCounter = 0;
        this.taskSessionsQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (taskSessionsQueue) {
                synchronized (workThreadCounter) {
                    while (SchedulerThread.getWorkThreadCounter() < workThreadThreshold && !(taskSessionsQueue.isEmpty())) {
                        SchedulerThread.setWorkThreadCounter(SchedulerThread.getWorkThreadCounter() + 1);
                        WorkThread workThread = new WorkThread(SchedulerThread.getTaskSession());
                        workThread.start();
                    }
                }
            }
        }
    }


    public static void addTaskSession(List<Task> taskSession) {
        synchronized (SchedulerThread.taskSessionsQueue) {
            taskSessionsQueue.add(taskSession);
        }
        WorkThread.sendUpdateOfTaskSessionQueue("TASK INSERT QUEUE UPDATE\n");
    }

    public static List<Task> getTaskSession() {
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

    public void stopSchedulerThread() {
        while (SchedulerThread.getWorkThreadCounter() != 0) {
            //Here we are waiting until all the running workthreads finish their task sessions
            //Kind of soft stopping
        }
        stop();
    }

}