/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui.controllers;

import com.remotelauncher.client.listeners.RequestListener;
import com.remotelauncher.client.listeners.ResponseListener;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import com.remotelauncher.client.gui.RemoteLauncher;
import com.sun.xml.internal.bind.marshaller.MinimumEscapeHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, ResponseListener {
    private final String PASSWORD_INCORRECT = "Password is incorrect!";
    private final String SERVER_ANAVAILABLE = "Server is anavailable now";
    private final String FILL_FIELDS = "Please, fill your name and password";

    private RemoteLauncher mainApp;
    private boolean isConnected = false;
    private RequestListener requestListener;
    private Response response;

    @FXML
    private Button connectButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField tokenTextfield;

    @FXML
    private PasswordField password;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!tokenTextfield.getText().isEmpty() && !password.getText().isEmpty()) {
                    statusLabel.setVisible(false);
                    Request request = new Request();
                    request.setParameter("type", MessageType.LOGIN);
                    request.setParameter("token", tokenTextfield.getText());
                    request.setParameter("password", password.getText());
                    requestListener.sendRequest(request);
                }
                else {
                    statusLabel.setText(FILL_FIELDS);
                    statusLabel.setVisible(true);
                }
            }
        });
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isConnected) {
                    Request request = new Request();
                    request.setParameter("type", MessageType.ADMINISTRATIVE);
                    request.setParameter("state", "DISCONNECT");
                    requestListener.sendRequest(request);
                }
                System.out.println("App is closed");
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @Override
    public void receiveResponse(Response response) {
        this.response = response;
        if (response != null) {
            String message = (String) response.getParameter("message");
            if (message != null) {
                System.out.printf((String) response.getParameter("message"));
                if (message.equals("incorrect-password")) {
                    statusLabel.setText(PASSWORD_INCORRECT);
                    statusLabel.setVisible(true);
                }
                else {
                    statusLabel.setVisible(false);
                    mainApp.openMainFrame();
                }
            }
        }
    }

    public void setStatusConnection(boolean isConnected) {
        this.isConnected = isConnected;
        statusLabel.setText(SERVER_ANAVAILABLE);
        statusLabel.setVisible(!isConnected);
        connectButton.setDisable(!isConnected);
        tokenTextfield.setDisable(!isConnected);
        password.setDisable(!isConnected);
    }

    public void setMainApp(RemoteLauncher mainApp) {
        this.mainApp = mainApp;
    }

    public void addRequestListener(RequestListener listener) {
        requestListener = listener;
    }

}
