/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.threads;

import com.remotelauncher.ServerConstants;
import com.remotelauncher.server.data.Task;
import com.remotelauncher.server.threads.communication.RequestThread;
import com.remotelauncher.shared.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WorkThread extends Thread {

    private Logger LOGGER = LoggerFactory.getLogger(WorkThread.class);

    private List<Task> taskSession;
    private List<String> filesToExecute;
    private List<String> outputFiles;

    public WorkThread(List<Task> task) {
        filesToExecute = new ArrayList<>();
        outputFiles = new ArrayList<>();
        this.taskSession = task;
    }

    @Override
    public void run() {
        LOGGER.info("WORKTHREAD {} IS STARTED! {}", this.getId(), SchedulerThread.getWorkThreadCounter());
        execute();
        SchedulerThread.setWorkThreadCounter(SchedulerThread.getWorkThreadCounter() - 1);
    }

    private String saveTaskToFile(Task task) {
        String filename = ServerConstants.PATH_TO_TASKS + "task_" + task.getUserId() + "_" + task.getTaskId() + ".bat";
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filename));
            try {
                bufferedOutputStream.write(task.getTask());
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }

    private byte[] readOutputFile(String fileNamePath) {
        byte[] data = new byte[0];
        try {
            data = new byte[(int) Files.size(Paths.get(fileNamePath))];
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileNamePath));
            inputStream.read(data, 0, data.length);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void execute() {
        Process execution;
        Runtime runtime = Runtime.getRuntime();
        for (Task task : taskSession) {
            String executeFile = saveTaskToFile(task);
            String outputFile = ServerConstants.PATH_TO_TASKS + "output_" + task.getUserId() + "_" + task.getTaskId() + ".txt";
            outputFiles.add(outputFile);
            filesToExecute.add(executeFile);
            try {
                try {
                    sleep(50000); // sleep for 50 sec - testing our tasks queue
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                execution = runtime.exec("cmd /C call " + executeFile + " > " + outputFile,
                        null,
                        new File(ServerConstants.PATH_TO_TASKS));
                execution.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.info("TASK {} COMPLETED", task.getTaskId());
            ServerThread.getDatabaseOperations().setTaskIsCompleted(task.getTaskId(), readOutputFile(outputFile));
            // TODO: remove next sleep
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deleteFile(outputFile);
            deleteFile(executeFile);
            sendUpdateOfTaskSessionQueue("TASK COMPLETE QUEUE UPDATE\n");
        }
    }

    public static void sendUpdateOfTaskSessionQueue(String message) {
        List<Thread> communicationThreads = ServerThread.getCommunicationThreads();

        for (Thread thread : communicationThreads) {
            if (thread instanceof RequestThread) {
                ((RequestThread) thread).getRequestProcessor().sendUpdateOfTaskSessionQueue(new Response(), message);
            }
        }
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
            LOGGER.trace("File {} has been deleted", path);
        } catch (FileSystemException e) {
            LOGGER.error("File has not been deleted: {}", e.getReason());
        } catch (IOException e) {
            LOGGER.error("File {} has not been deleted", path);
        }
    }

}