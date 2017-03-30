/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

import com.remotelauncher.client.gui.RemoteLauncher;
import javafx.application.Application;

public class ClientEntryPoint {

    public static void main(String[] args) {
        Application.launch(RemoteLauncher.class);
    }

}
