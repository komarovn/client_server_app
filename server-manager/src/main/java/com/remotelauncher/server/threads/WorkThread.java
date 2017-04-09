/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.ServerConstants;
import com.remotelauncher.server.data.TaskSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WorkThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(WorkThread.class);

    private TaskSession taskSession;
    private List<String> filesToExecute;
    private List<String> outputFiles;

    public WorkThread(TaskSession taskSession) {
        filesToExecute = new ArrayList<>();
        outputFiles = new ArrayList<>();
        this.taskSession = taskSession;
    }

    @Override
    public void run() {
        LOGGER.info("WORKTHREAD {} IS STARTED! {}", this.getId(), SchedulerThread.getWorkThreadCounter());
        execute();
        SchedulerThread.setWorkThreadCounter(SchedulerThread.getWorkThreadCounter() - 1);
    }

    private String saveTaskToFile() {
        String filename = ServerConstants.PATH_TO_TASKS + "task_" + taskSession.getUserId() + "_" + taskSession.getTaskId() + ".bat";
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
            try {
                bufferedOutputStream.write(taskSession.getTask());
                bufferedOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private byte[] readOutputFile(String fileName) {
        byte[] data = new byte[(int) fileName.length()];
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
            inputStream.read(data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void execute() {
        Process execution = null;
        Runtime runtime = Runtime.getRuntime();
        String executeFile = this.saveTaskToFile();
        String outputFile = "task_" + taskSession.getUserId() + "_" + taskSession.getTaskId() + ".output";
        outputFiles.add(outputFile);
        filesToExecute.add(executeFile);
        try {
            execution = runtime.exec("cmd /c cd " + ServerConstants.PATH_TO_TASKS + " & cmd /c call " + executeFile + " > " + outputFile);
            execution.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("TASK {} COMPLETED", taskSession.getTaskId());
        ServerThread.getDatabaseOperations().setTaskIsCompleted(taskSession.getTaskId(), readOutputFile(ServerConstants.PATH_TO_TASKS + outputFile));
        File file = new File(ServerConstants.PATH_TO_TASKS + outputFile);
        file.delete();
        file = new File(executeFile);
        file.delete();
        //TODO: delete correctly
    }

}