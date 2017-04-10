/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.server.data.TaskSession;
import com.remotelauncher.server.threads.SchedulerThread;
import com.remotelauncher.server.threads.ServerThread;
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
            String token = (String) request.getParameter("token");
            String password = (String) request.getParameter("password");
            if (ServerThread.getDatabaseOperations().isUserExists(token)) {
                if (ServerThread.getDatabaseOperations().checkPasswordForUser(token, password)) {
                    response.setParameter("message", "accept");
                    userId = ServerThread.getDatabaseOperations().getUserIdByName(token);
                    LOGGER.info("User {} has been connected", userId);
                    //TODO: send queue update
                } else {
                    response.setParameter("message", "incorrect-password");
                }
            } else {
                ServerThread.getDatabaseOperations().createNewUser(token, password);
                userId = ServerThread.getDatabaseOperations().getUserIdByName(token);
                LOGGER.info("Create new user with id {}", userId);
                response.setParameter("message", "accept-new-user");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(Request request, Response response) {
        MessageType type = (MessageType) request.getParameter("type");
        if (type != null) {
            switch (type) {
                case LOGIN:
                    receiveToken(request, response);
                    break;
                case TASKSESSION:
                    receiveTaskSession(request, response);
                    break;
                case ADMINISTRATIVE:
                    break;
                default:
                    unrecognizedMessageType(response);
            }
        } else {
            nullMessageType(response);
        }
    }

    private void unrecognizedMessageType(Response response) {
        response.setParameter("type", MessageType.ADMINISTRATIVE);
        response.setParameter("message", "Can't recognise your request! Check the guide to provide readable request");
    }

    private void nullMessageType(Response response) {
        response.setParameter("type", MessageType.ADMINISTRATIVE);
        response.setParameter("message", "No message type! Check the guide to provide readable request");
    }

    private void receiveTaskSession(Request request, Response response) {
        int taskFileSize = (Integer) (request.getParameter("taskFileSize"));
        byte[] data = (byte[]) request.getParameter("taskFile");
        String name = (String) request.getParameter("taskName");
        String format = (String) request.getParameter("taskFormatType");
        try {
            String taskId = ServerThread.getDatabaseOperations().insertNewTask(data, name, userId, format);
            TaskSession taskSession = new TaskSession(taskId, userId, data);
            SchedulerThread.addTaskSession(taskSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
