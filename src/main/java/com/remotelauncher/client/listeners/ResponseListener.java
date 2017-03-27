/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.listeners;

import com.remotelauncher.shared.Response;

public interface ResponseListener {

    void receiveResponse(Response response);

}
