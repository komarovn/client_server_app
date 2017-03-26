/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui.controllers;

import com.remotelauncher.client.CommunicationListener;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import com.remotelauncher.client.gui.RemoteLauncher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private RemoteLauncher mainApp;
    private CommunicationListener listener;

    @FXML
    private Button connectButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField tokenTextfield;

    @FXML
    private Label serverUnavailable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO: open main app form
                if (!tokenTextfield.getText().isEmpty()) {
                    Request request = new Request();
                    String token = tokenTextfield.getText();
                    request.setParameter("token", token);
                    Response response = listener.processRequest(request);
                    if (response.getParameter("message") != null) {
                        System.out.printf((String) response.getParameter("message"));
                        mainApp.openMainFrame();
                    }
                }
            }
        });
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Request request = new Request();
                request.setParameter("state", "DISCONNECT");
                listener.processRequest(request);
                System.out.println("App is closed");
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void setStatusConnection(boolean isConnected) {
        serverUnavailable.setVisible(!isConnected);
        connectButton.setDisable(!isConnected);
        tokenTextfield.setDisable(!isConnected);
    }

    public void setMainApp(RemoteLauncher mainApp) {
        this.mainApp = mainApp;
    }

    public void addListener(CommunicationListener listener) {
        this.listener = listener;
    }

}
