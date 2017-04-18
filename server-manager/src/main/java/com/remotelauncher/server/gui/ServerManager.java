/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.gui;

import com.remotelauncher.TCPServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class ServerManager extends Application {

    private final String SERVER_MANAGER = "Server Manager";
    private TCPServer tcpServer;
    private ServerController controller;

    public ServerManager() {}

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL address = getClass().getResource("/fxml/ServerGUI.fxml");
        FXMLLoader loader = new FXMLLoader(address);
        Parent root = loader.load();
        primaryStage.setTitle(SERVER_MANAGER);
        primaryStage.setScene(new Scene(root));
        controller = loader.getController();
        controller.setMainApp(this);

        controller.setServerState(false);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (tcpServer != null) {
                    tcpServer.stopServer();
                }
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();
    }

    public void runTCPServer() {
        tcpServer = new TCPServer();
        tcpServer.runServer();
    }

    public void stopTCPServer() {
        Thread stopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                tcpServer.stopServer();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.setStatusStopped();
                    }
                });
            }
        });
        stopThread.start();
    }

}
