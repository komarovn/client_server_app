/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.data;

public class Task {

    private String taskId;
    private String userId;
    private String outputId;
    private byte[] task;
    private boolean isDone;

    public Task(String taskId, String userId, byte[] task) {
        this.taskId = taskId;
        this.userId = userId;
        this.task = task;
        this.isDone = false;
    }

    public byte[] getTask() {
        return task;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

}
