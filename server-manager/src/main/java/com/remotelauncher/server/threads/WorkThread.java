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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
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
        //TODO: send update of queue to all
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
        Process execution;
        Runtime runtime = Runtime.getRuntime();
        String executeFile = saveTaskToFile();
        String outputFile = ServerConstants.PATH_TO_TASKS + "output_" + taskSession.getUserId() + "_" + taskSession.getTaskId() + ".txt";
        outputFiles.add(outputFile);
        filesToExecute.add(executeFile);
        try {
            execution = runtime.exec("cmd /C call " + executeFile + " > " + outputFile, null, new File(ServerConstants.PATH_TO_TASKS));
            execution.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("TASK {} COMPLETED", taskSession.getTaskId());
        ServerThread.getDatabaseOperations().setTaskIsCompleted(taskSession.getTaskId(), readOutputFile(outputFile));
        File file = new File(outputFile);
        file.delete();
        file = new File(executeFile);
        file.delete();
        //TODO: delete correctly
        sendUpdateOfTaskSessionQueue("TASK COMPLETE QUEUE UPDATE\n");
    }

    public static void sendUpdateOfTaskSessionQueue(String message) {
        List<Thread> communicationThreads = ServerThread.getCommunicationThreads();
        List<HashMap<String, Object>> queueUpdate = ServerThread.getDatabaseOperations().getQueueUpdateOfUndoneTaskSessions();

        Response response = new Response();
        response.setParameter("type", MessageType.QUEUEUPDATE);
        response.setParameter("queue", queueUpdate);
        response.setParameter("message", message);
        for (Thread thread : communicationThreads) {
            if (thread instanceof ResponseThread) {
                ((ResponseThread) thread).sendResponse(response);
            }
        }
    }

}