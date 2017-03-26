/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;

public interface CommunicationListener {

    /**
     *
     * @param request - sending data to server
     * @return server's response
     */
    Response processRequest(Request request);

}
