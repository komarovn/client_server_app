/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui.controllers;

import com.remotelauncher.client.RequestListener;
import com.remotelauncher.client.ResponseListener;
import com.remotelauncher.client.gui.RemoteLauncher;
import com.remotelauncher.shared.Response;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class RemoteLauncherController implements Initializable, ResponseListener {

    private RemoteLauncher mainApp;
    private RequestListener requestListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void receiveResponse(Response response) {

    }

    public void setMainApp(RemoteLauncher mainApp) {
        this.mainApp = mainApp;
    }

    public void addRequestListener(RequestListener listener) {
        requestListener = listener;
    }

}
