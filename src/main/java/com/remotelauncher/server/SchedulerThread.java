package com.remotelauncher.server;

/**
 * Created by rpovelik on 18/03/2017.
 */
public class SchedulerThread extends Thread {

    private WorkThread[] threadPool;

    public WorkThread[] getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(WorkThread[] threadPool) {
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
