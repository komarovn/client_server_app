/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.server.threads.ServerThread;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;

public class RequestProcessor {

    private void receiveToken(Request token, Response response) {
        try {
            String tok = (String) token.getParameter("token");
            //TODO: put user's token to DB.
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setParameter("message", "Welcome!");
    }

    private String receiveUserId(String token) {
        String userId = Integer.toString(token.hashCode());
        return userId;
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
        try {
            Blob blob = new SerialBlob(data);
            String query = "INSERT INTO remotelauncher.tasks (`task`, `is_completed`, `user_id`) VALUES(?, 0, null)";
            ServerThread.getDatabaseOperations().executeStatement(query, blob);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
