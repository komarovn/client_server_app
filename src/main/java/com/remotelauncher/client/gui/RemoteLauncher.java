/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui;

import com.remotelauncher.client.StringResourses;
import com.remotelauncher.client.TCPClient;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import com.remotelauncher.client.gui.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class RemoteLauncher extends Application {

    private TCPClient tcpClient;
    private Boolean isConnected = false;

    public RemoteLauncher() {
        tcpClient = new TCPClient();
        tcpClient.runClient();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL address = getClass().getResource("/fxml/LoginFormGUI.fxml");
        FXMLLoader loader = new FXMLLoader(address);
        Parent root = loader.load();
        primaryStage.setTitle(StringResourses.REMOTE_LAUNCHER);
        primaryStage.setScene(new Scene(root));
        LoginController controller = loader.getController();
        controller.setMainApp(this);

        isConnected = tcpClient.getConnected();
        controller.setStatusConnection(isConnected);

        primaryStage.show();
    }

    public Response processRequest(Request request) {
        return tcpClient.processRequest(request);
    }
}
