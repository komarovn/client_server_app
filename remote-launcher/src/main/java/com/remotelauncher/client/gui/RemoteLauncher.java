/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui;

import com.remotelauncher.ClientConstants;
import com.remotelauncher.client.PresenterManager;
import com.remotelauncher.client.threads.communication.RequestThread;
import com.remotelauncher.client.threads.communication.ResponseThread;
import com.remotelauncher.StringResourses;
import com.remotelauncher.TCPClient;
import com.remotelauncher.client.gui.controllers.RemoteLauncherController;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.client.gui.controllers.LoginController;
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
        isConnected = tcpClient.getConnected();
        if (isConnected) {
            responseThread = new ResponseThread(tcpClient.getClientSocket());
            requestThread = new RequestThread(tcpClient.getClientSocket());
            responseThread.start();
            requestThread.start();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        URL address = getClass().getResource("/fxml/LoginFormGUI.fxml");
        FXMLLoader loader = new FXMLLoader(address);
        Parent root = loader.load();
        primaryStage.setTitle(StringResourses.REMOTE_LAUNCHER);
        primaryStage.setScene(new Scene(root));

        initializeLoginController(loader);

        primaryStage.show();
    }

    private void initializeLoginController(FXMLLoader loader) {
        LoginController controller = loader.getController();
        controller.setMainApp(this);

        PresenterManager<LoginController> presenterManager = new PresenterManager<>();
        presenterManager.setController(controller);
        controller.addRequestListener(requestThread);
        if (responseThread != null) {
            responseThread.addResponseListener(presenterManager);
        }

        controller.setStatusConnection(isConnected);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (tcpClient.getClientSocket() != null && !tcpClient.getClientSocket().isClosed()) {
                    Request request = new Request();
                    request.setParameter(ClientConstants.TYPE, MessageType.ADMINISTRATIVE);
                    request.setParameter(ClientConstants.CLIENT_STATE, "DISCONNECT");
                    requestThread.sendRequest(request);
                }
                System.out.println("App is closed");
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void openMainFrame(String userId) {
        try {
            URL address = getClass().getResource("/fxml/RemoteLauncherGUI.fxml");
            FXMLLoader loader = new FXMLLoader(address);
            Parent root = loader.load();
            RemoteLauncherController controller = loader.getController();
            controller.setMainApp(this);
            controller.setUserId(userId);

            PresenterManager<RemoteLauncherController> presenterManager = new PresenterManager<>();
            presenterManager.setController(controller);
            controller.addRequestListener(requestThread);
            controller.loadUserData();
            responseThread.addResponseListener(presenterManager);

            stage.getScene().setRoot(root);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
