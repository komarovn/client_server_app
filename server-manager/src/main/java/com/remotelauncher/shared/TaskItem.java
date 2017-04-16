/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.shared;

import java.io.Serializable;

public class TaskItem implements Serializable {
    private String taskName;
    private String fileType;
    private Integer fileLength;
    private byte[] data;

    public TaskItem(String taskName, String fileType, Integer fileLength, byte[] data) {
        this.taskName = taskName;
        this.fileType = fileType;
        this.fileLength = fileLength;
        this.data = data;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getFileLength() {
        return fileLength;
    }

    public void setFileLength(Integer fileLength) {
        this.fileLength = fileLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Tasktem: { " +
                "taskName: " + taskName +
                ", fileType: " + fileType +
                ", fileLength: " + fileLength +
                ", data: byte[]@" + data.hashCode() +
                "}";
    }
}
