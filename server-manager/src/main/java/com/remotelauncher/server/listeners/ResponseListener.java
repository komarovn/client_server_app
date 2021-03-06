/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.listeners;

import com.remotelauncher.shared.Response;

public interface ResponseListener {

    void sendResponse(Response response);

}
