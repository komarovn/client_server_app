/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui;

import com.remotelauncher.client.RequestThread;
import com.remotelauncher.client.ResponseThread;
import com.remotelauncher.client.StringResourses;
import com.remotelauncher.client.TCPClient;
import com.remotelauncher.shared.Request;
import com.remotelauncher.client.gui.controllers.LoginController;
import com.remotelauncher.shared.Response;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

public class RemoteLauncher extends Application {

    private TCPClient tcpClient;
    private ResponseThread responseThread;
    private RequestThread requestThread;
    private Boolean isConnected = false;
    private Stage stage;

    public RemoteLauncher() {
        tcpClient = new TCPClient();
        tcpClient.runClient();
        responseThread = new ResponseThread(tcpClient.getClientSocket());
        requestThread = new RequestThread(tcpClient.getClientSocket());
        responseThread.start();
        requestThread.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Request request = new Request();
                request.setParameter("state", "DISCONNECT");
                if (!tcpClient.getClientSocket().isClosed()) {
                    requestThread.sendRequest(request);
                }
                System.out.println("App is closed");
                Platform.exit();
                System.exit(0);
            }
        });
        URL address = getClass().getResource("/fxml/LoginFormGUI.fxml");
        FXMLLoader loader = new FXMLLoader(address);
        Parent root = loader.load();
        primaryStage.setTitle(StringResourses.REMOTE_LAUNCHER);
        primaryStage.setScene(new Scene(root));
        LoginController controller = loader.getController();
        controller.setMainApp(this);

        controller.addRequestListener(requestThread);
        responseThread.addResponseListener(controller);

        isConnected = tcpClient.getConnected();
        controller.setStatusConnection(isConnected);

        primaryStage.show();
    }

    public void openMainFrame() {
        try {
            URL address = getClass().getResource("/fxml/RemoteLauncherGUI.fxml");
            FXMLLoader loader = new FXMLLoader(address);
            Parent root = loader.load();
            stage.getScene().setRoot(root);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
