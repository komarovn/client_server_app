package com.remotelauncher;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResponseExecutor /*extends AsyncTask<Void, Void, Void>*/ {
    private Activity mainActivity;

    public ResponseExecutor(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void receiveResponse(Response response) {
        MessageType type = (MessageType) response.getParameter(ClientConstants.TYPE);
        if (type != null) {
            switch (type) {
                case LOGIN:
                    processLoginResponse(response);
                    break;
                case ADMINISTRATIVE:
                    break;
                case QUEUEUPDATE:
                    processQueueUpdateResponse(response);
                    break;
            }
        }
    }

    private void processLoginResponse(Response response) {
        if (response.getParameter(ClientConstants.MESSAGE) != null) {
            String message = (String) response.getParameter(ClientConstants.MESSAGE);
            if (message.equals("incorrect-password")) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) mainActivity).setLoginStatusLabel("Password is incorrect!");
                    }
                });
            }
            else {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) mainActivity).openMainFrame();
                    }
                });
            }
            System.out.printf(message);
        }
    }

    private void processQueueUpdateResponse(Response response) {
        List<HashMap<String, Object>> queue = (List<HashMap<String, Object>>) response.getParameter(ClientConstants.QUEUE);
        final List<CellView> tasks = new ArrayList<>();
        for (HashMap<String, Object> task : queue) {
            String taskName = (String) task.get(ClientConstants.TASK_NAME);
            Integer taskId = (Integer) task.get(ClientConstants.TASK_ID);
            Integer userId = (Integer) task.get(ClientConstants.TASK_USER_ID);
            tasks.add(new CellView(taskName, taskId, userId));
        }
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) mainActivity).setTaskQueue(tasks);
            }
        });
    }
}
