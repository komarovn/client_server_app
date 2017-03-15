/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui;

import com.remotelauncher.client.gui.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class RemoteLauncher extends Application {

    private Boolean isConnected = false;
    private String token;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL address = getClass().getResource("/fxml/LoginFormGUI.fxml");
        FXMLLoader loader = new FXMLLoader(address);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        LoginController controller = loader.getController();
        controller.setMainApp(this);

        Parameters parameters = getParameters();
        if (!parameters.getRaw().isEmpty()) {
            isConnected = Boolean.valueOf(parameters.getRaw().get(0));
        }
        controller.setStatusConnection(isConnected);

        primaryStage.show();
    }

    public void setToken(String token) {
        this.token = token;
    }
}
