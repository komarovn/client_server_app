/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import com.remotelauncher.ClientConstants;
import com.remotelauncher.client.gui.controllers.CellView;
import com.remotelauncher.client.gui.controllers.LoginController;
import com.remotelauncher.client.gui.controllers.RemoteLauncherController;
import com.remotelauncher.client.listeners.ResponseListener;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PresenterManager<Controller> implements ResponseListener {
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void receiveResponse(Response response) {
        MessageType type = (MessageType) response.getParameter(ClientConstants.TYPE);
        if (type != null) {
            switch (type) {
                case LOGIN:
                    if (controller instanceof LoginController) {
                        processLoginResponse(response);
                    }
                    break;
                case ADMINISTRATIVE:
                    break;
                case QUEUEUPDATE:
                    if (controller instanceof RemoteLauncherController) {
                        processQueueUpdateResponse(response);
                    }
                    break;
                case DLRESULT:
                    if (controller instanceof RemoteLauncherController) {
                        processDownloadResult(response);
                    }
                    break;
            }
        }
    }

    private void processLoginResponse(Response response) {
        if (response.getParameter(ClientConstants.MESSAGE) != null) {
            String message = (String) response.getParameter(ClientConstants.MESSAGE);
            if (message.equals("incorrect-password")) {
                ((LoginController) controller).setPasswordIncorrect();
            }
            else {
                ((LoginController) controller).openMainFrame((String) response.getParameter(ClientConstants.USER_ID));
            }
            System.out.printf(message);
        }
    }

    private void processQueueUpdateResponse(Response response) {
        List<HashMap<String, Object>> queue = (List<HashMap<String, Object>>) response.getParameter(ClientConstants.QUEUE);
        List<CellView> tasks = new ArrayList<>();
        for (HashMap<String, Object> task : queue) {
            String taskName = (String) task.get(ClientConstants.TASK_NAME);
            Integer taskId = (Integer) task.get(ClientConstants.TASK_ID);
            Integer userId = (Integer) task.get(ClientConstants.TASK_USER_ID);
            tasks.add(new CellView(taskName, taskId, userId));
        }
        ((RemoteLauncherController) controller).setTaskQueue(tasks);
    }

    private void processDownloadResult(Response response) {
        byte[] data = (byte[]) response.getParameter(ClientConstants.RESULT_FILE);
        ((RemoteLauncherController) controller).downloadResult(data);
    }

}
