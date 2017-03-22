/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui.controllers;

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
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button connectButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField tokenTextfield;

    @FXML
    private Label serverUnavailable;

    private RemoteLauncher mainApp;

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
                    Response response = mainApp.processRequest(request);
                    if (response.getParameter("message") != null) {
                        System.out.printf((String) response.getParameter("message"));
                    }
                }
            }
        });
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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

}
