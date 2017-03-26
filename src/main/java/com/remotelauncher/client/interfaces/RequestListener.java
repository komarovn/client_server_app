/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.interfaces;

import com.remotelauncher.shared.Request;

public interface RequestListener {

    void sendRequest(Request request);

}
