/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui.controllers;

import com.remotelauncher.ClientConstants;
import com.remotelauncher.client.listeners.RequestListener;
import com.remotelauncher.client.listeners.ResponseListener;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import com.remotelauncher.client.gui.RemoteLauncher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
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
        tokenTextfield.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    connectAction();
                }
            }
        });
        password.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    connectAction();
                }
            }
        });
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connectAction();
            }
        });
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isConnected) {
                    Request request = new Request();
                    request.setParameter(ClientConstants.TYPE, MessageType.ADMINISTRATIVE);
                    request.setParameter(ClientConstants.CLIENT_STATE, "DISCONNECT");
                    requestListener.sendRequest(request);
                }
                System.out.println("App is closed");
                Platform.exit();
                System.exit(0);
            }
        });
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

    public void setPasswordIncorrect() {
        statusLabel.setText(PASSWORD_INCORRECT);
        statusLabel.setVisible(true);
    }

    public void openMainFrame(String userId) {
        statusLabel.setVisible(false);
        mainApp.openMainFrame(userId);
    }

    private void connectAction() {
        if (!tokenTextfield.getText().isEmpty() && !password.getText().isEmpty()) {
            statusLabel.setVisible(false);
            Request request = new Request();
            request.setParameter(ClientConstants.TYPE, MessageType.LOGIN);
            request.setParameter(ClientConstants.USER_NAME, tokenTextfield.getText());
            request.setParameter(ClientConstants.PASSWORD, password.getText());
            requestListener.sendRequest(request);
        }
        else {
            statusLabel.setText(FILL_FIELDS);
            statusLabel.setVisible(true);
        }
    }

}
