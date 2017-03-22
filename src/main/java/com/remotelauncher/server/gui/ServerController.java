/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    private final String SERVER_RUNNING = "Running";
    private final String SERVER_OFF = "Off";
    private final String SERVER_SHUTTING_DOWN = "Shutting Down";

    @FXML
    private Button startServer;

    @FXML
    private Button stopServer;

    @FXML
    private Pane statusColor;

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea consoleOutput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startServer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                statusLabel.setText(SERVER_RUNNING);
                statusColor.setStyle("-fx-background-color: green; -fx-background-radius: 20px");
            }
        });
        stopServer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                statusLabel.setText(SERVER_OFF);
                statusColor.setStyle("-fx-background-color: red; -fx-background-radius: 20px");
            }
        });
    }
}
