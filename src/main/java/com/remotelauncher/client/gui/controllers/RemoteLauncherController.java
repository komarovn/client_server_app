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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class RemoteLauncherController implements Initializable, ResponseListener {

    private RemoteLauncher mainApp;
    private RequestListener requestListener;
    private ObservableList taskQueueItems;

    @FXML
    private Button loadFile;

    @FXML
    private Button createTask;

    @FXML
    private ListView taskQueue;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taskQueueItems = FXCollections.observableArrayList("One", "Two", "Three", "Four");
        taskQueue.setItems(taskQueueItems);
    }

    public void setMainApp(RemoteLauncher mainApp) {
        this.mainApp = mainApp;
    }

    public void addRequestListener(RequestListener listener) {
        requestListener = listener;
    }

    @Override
    public void receiveResponse(Response response) {

    }
}
