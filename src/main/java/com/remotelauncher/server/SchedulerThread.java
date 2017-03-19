package com.remotelauncher.server;

import java.util.Queue;

/**
 * Created by rpovelik on 18/03/2017.
 */
public class SchedulerThread extends Thread {

    private WorkThread[] threadPool;
    private Queue<TaskSession> taskSessionsQueue;

    public Queue<TaskSession> getTaskSessionsQueue() {
        return taskSessionsQueue;
    }

    public void setTaskSessionsQueue(Queue<TaskSession> taskSessionsQueue) {
        this.taskSessionsQueue = taskSessionsQueue;
    }

    private WorkThread[] getThreadPool() {
        return threadPool;
    }

    private void setThreadPool(WorkThread[] threadPool) {
        this.threadPool = threadPool;
    }

    public SchedulerThread() {
        for(WorkThread workThread : this.getThreadPool()){
            workThread = new WorkThread();
            //TODO: Do we need to set them like daemons?
            workThread.setDaemon(true);
            workThread.start();
        }
    }

    @Override
    public void run() {
        super.run();
    }
}
