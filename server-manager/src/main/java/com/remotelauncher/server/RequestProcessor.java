/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;

public class RequestProcessor {

    private void receiveToken(Request token) {
        try {
            String tok = (String) token.getParameter("token");
            //TODO: put user's token to DB.
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    receiveToken(request);
                    response.setParameter("message", "Welcome!");
                    break;
                case TASKSESSION:

                    break;
                case ADMINISTRATIVE:
                    break;
                default:
                    response.setParameter("type", MessageType.ADMINISTRATIVE);
                    response.setParameter("message", "Can't recognise your request! Check the guide to provide readable request");
            }
        }
    }
}
