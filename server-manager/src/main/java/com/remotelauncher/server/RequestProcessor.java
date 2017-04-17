/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.ServerConstants;
import com.remotelauncher.server.data.Task;
import com.remotelauncher.server.threads.SchedulerThread;
import com.remotelauncher.server.threads.ServerThread;
import com.remotelauncher.server.threads.communication.RequestThread;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import com.remotelauncher.shared.TaskItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * One client has his own Request Processor
 */
public class RequestProcessor {
    private Logger LOGGER = LoggerFactory.getLogger(RequestProcessor.class);

    private RequestThread owner;
    private String userId;
    private boolean showUncompletedTasks = true;
    private boolean showMyTasks = true;

    public RequestProcessor(RequestThread owner) {
        this.owner = owner;
    }

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
                    receiveRequestForFilter(request, response);
                    break;
                case LOADDATA:
                    loadUsersData(response);
                    break;
                case DLRESULT:
                    receiveDownloadResult(request, response);
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
        List<TaskItem> taskSessionRequest = (List<TaskItem>) request.getParameter(ServerConstants.TASK_SESSION);
        List<Task> taskSession = new ArrayList<>();
        for (TaskItem taskItem : taskSessionRequest) {
            int taskFileSize = taskItem.getFileLength();
            byte[] data = taskItem.getData();
            String name = taskItem.getTaskName();
            String format = taskItem.getFileType();
            SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String taskId = ServerThread.getDatabaseOperations().insertNewTask(data, name, userId, format, dateTime.format(new Date()));
            taskSession.add(new Task(taskId, userId, data));
        }
        SchedulerThread.addTaskSession(taskSession);
        /*int taskFileSize = (Integer) (request.getParameter(ServerConstants.TASK_FILE_SIZE));
        byte[] data = (byte[]) request.getParameter(ServerConstants.TASK_FILE);
        String name = (String) request.getParameter(ServerConstants.TASK_NAME);
        String format = (String) request.getParameter(ServerConstants.TASK_FORMAT_TYPE);*/
    }

    private void receiveRequestForFilter(Request request, Response response) {
        showMyTasks = (Boolean) request.getParameter(ServerConstants.SHOW_MY_TASKS_ONLY);
        showUncompletedTasks = (Boolean) request.getParameter(ServerConstants.SHOW_UNCOMPLETED_TASKS);
        updateTaskSessionQueue(response, "Apply filter\n");
    }

    private void loadUsersData(Response response) {
        response.setParameter(ServerConstants.TYPE, MessageType.QUEUEUPDATE);
        updateTaskSessionQueue(response, "Load data\n");
    }

    private void updateTaskSessionQueue(Response response, String message) {
        List<HashMap<String, Object>> queueUpdate = ServerThread.getDatabaseOperations().getQueueUpdateOfTaskSessions(!showUncompletedTasks, showMyTasks ? userId : null);
        response.setParameter(ServerConstants.TYPE, MessageType.QUEUEUPDATE);
        response.setParameter(ServerConstants.QUEUE, queueUpdate);
        response.setParameter(ServerConstants.MESSAGE, message);
    }

    private void receiveDownloadResult(Request request, Response response) {
        byte[] data = ServerThread.getDatabaseOperations().getResultFile((Integer) request.getParameter(ServerConstants.TASK_ID));
        response.setParameter(ServerConstants.TYPE, MessageType.DLRESULT);
        response.setParameter(ServerConstants.RESULT_FILE, data);
    }

    /* Use outside of this class only */
    public void sendUpdateOfTaskSessionQueue(Response response, String message) {
        updateTaskSessionQueue(response, message);
        owner.sendResponse(response);
    }
}
