/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

import com.remotelauncher.server.gui.ServerManager;
import javafx.application.Application;

public class ServerEntryPoint {

    public static void main(String[] args) {
        Application.launch(ServerManager.class);
    }

}
