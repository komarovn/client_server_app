/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    private final String SERVER_RUNNING = "Running";
    private final String SERVER_OFF = "Off";
    private final String SERVER_SHUTTING_DOWN = "Shutting Down";
    private ServerManager mainApp;

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

    public void setMainApp(ServerManager mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startServer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                statusLabel.setText(SERVER_RUNNING);
                statusColor.setStyle("-fx-background-color: green; -fx-background-radius: 20px");
                mainApp.runTCPServer();
                startServer.setDisable(true);
            }
        });
        stopServer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                statusLabel.setText(SERVER_OFF);
                statusColor.setStyle("-fx-background-color: red; -fx-background-radius: 20px");
                mainApp.stopTCPServer();
                startServer.setDisable(false);
            }
        });
        consoleOutput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                consoleOutput.setScrollTop(Double.MAX_VALUE);
            }
        });
        OutputStream console = new OutputStream() {
            @Override
            public void write(final int b) throws IOException {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        consoleOutput.appendText(String.valueOf((char) b));
                    }
                });
            }
        };
        PrintStream printStream = new PrintStream(console, true);
        System.setOut(printStream);
        System.setErr(printStream);
    }
}
