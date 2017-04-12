/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.ServerConstants;
import com.remotelauncher.server.data.TaskSession;
import com.remotelauncher.server.threads.SchedulerThread;
import com.remotelauncher.server.threads.ServerThread;
import com.remotelauncher.server.threads.WorkThread;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;

/**
 * One client has his own Request Processor
 */
public class RequestProcessor {
    private Logger LOGGER = LoggerFactory.getLogger(RequestProcessor.class);

    private String userId;

    private void receiveToken(Request request, Response response) {
        try {
            String token = (String) request.getParameter(ServerConstants.USER_NAME);
            String password = (String) request.getParameter(ServerConstants.PASSWORD);
            if (ServerThread.getDatabaseOperations().isUserExists(token)) {
                if (ServerThread.getDatabaseOperations().checkPasswordForUser(token, password)) {
                    response.setParameter(ServerConstants.MESSAGE, "accept");
                    userId = ServerThread.getDatabaseOperations().getUserIdByName(token);
                    LOGGER.info("User {} has been connected", userId);
                    response.setParameter(ServerConstants.USER_ID, userId);
                    //TODO: send queue update
                } else {
                    response.setParameter(ServerConstants.MESSAGE, "incorrect-password");
                }
            } else {
                ServerThread.getDatabaseOperations().createNewUser(token, password);
                userId = ServerThread.getDatabaseOperations().getUserIdByName(token);
                LOGGER.info("Create new user with id {}", userId);
                response.setParameter(ServerConstants.MESSAGE, "accept-new-user");
                response.setParameter(ServerConstants.USER_ID, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(Request request, Response response) {
        MessageType type = (MessageType) request.getParameter(ServerConstants.TYPE);
        if (type != null) {
            switch (type) {
                case LOGIN:
                    receiveToken(request, response);
                    response.setParameter(ServerConstants.TYPE, MessageType.LOGIN);
                    break;
                case TASKSESSION:
                    receiveTaskSession(request, response);
                    response.setParameter(ServerConstants.TYPE, MessageType.TASKSESSION);
                    break;
                case ADMINISTRATIVE:
                    response.setParameter(ServerConstants.TYPE, MessageType.ADMINISTRATIVE);
                    break;
                case FILTERQUEUE:
                    receiveRequestForFilter(request);
                    break;
                default:
                    unrecognizedMessageType(response);
            }
        } else {
            nullMessageType(response);
        }
    }

    private void unrecognizedMessageType(Response response) {
        response.setParameter(ServerConstants.TYPE, MessageType.ADMINISTRATIVE);
        response.setParameter(ServerConstants.MESSAGE, "Can't recognise your request! Check the guide to provide readable request");
    }

    private void nullMessageType(Response response) {
        response.setParameter(ServerConstants.TYPE, MessageType.ADMINISTRATIVE);
        response.setParameter(ServerConstants.MESSAGE, "No message type! Check the guide to provide readable request");
    }

    private void receiveTaskSession(Request request, Response response) {
        int taskFileSize = (Integer) (request.getParameter(ServerConstants.TASK_FILE_SIZE));
        byte[] data = (byte[]) request.getParameter(ServerConstants.TASK_FILE);
        String name = (String) request.getParameter(ServerConstants.TASK_NAME);
        String format = (String) request.getParameter(ServerConstants.TASK_FORMAT_TYPE);
        try {
            String taskId = ServerThread.getDatabaseOperations().insertNewTask(data, name, userId, format);
            TaskSession taskSession = new TaskSession(taskId, userId, data);
            SchedulerThread.addTaskSession(taskSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveRequestForFilter(Request request) {
        //WorkThread.setShowMyTasks((Boolean) request.getParameter(ServerConstants.SHOW_MY_TASKS_ONLY));
        //WorkThread.setShowUncompletedTasks((Boolean) request.getParameter(ServerConstants.SHOW_UNCOMPLETED_TASKS));
        WorkThread.sendUpdateOfTaskSessionQueue("Apply filter\n");
    }
}
