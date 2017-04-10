/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui.controllers;

import com.remotelauncher.client.ControllerHelper;
import com.remotelauncher.client.listeners.RequestListener;
import com.remotelauncher.client.listeners.ResponseListener;
import com.remotelauncher.client.gui.RemoteLauncher;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.shared.Response;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RemoteLauncherController implements Initializable {
    private final String CHOOSE_A_FILE = "Choose a File...";

    private RemoteLauncher mainApp;
    private RequestListener requestListener;
    private ObservableList taskQueueItems = FXCollections.observableArrayList();

    @FXML
    private Button loadFile;

    @FXML
    private TextField filePath;

    @FXML
    private TextField taskName;

    @FXML
    private Button createTask;

    @FXML
    private ListView taskQueue;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taskQueue.setItems(taskQueueItems);

        loadFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openChooseFileDialog();
            }
        });
        filePath.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (filePath.getText().equals(CHOOSE_A_FILE)) {
                    openChooseFileDialog();
                }
            }
        });
        filePath.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (filePath.getText().equals(CHOOSE_A_FILE) && taskName.getText().isEmpty()) {
                    createTask.setDisable(true);
                } else {
                    createTask.setDisable(false);
                }
            }
        });
        createTask.setDisable(true);
        createTask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendFile();
                filePath.setText(CHOOSE_A_FILE);
            }
        });
    }

    public void setMainApp(RemoteLauncher mainApp) {
        this.mainApp = mainApp;
    }

    public void addRequestListener(RequestListener listener) {
        requestListener = listener;
    }

    private void openChooseFileDialog() {
        FileChooser openFileDialog = new FileChooser();
        openFileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.bat", "*.java"));
        openFileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("Batch files (*.bat)", "*.bat"));
        openFileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java files (*.java)", "*.java"));
        File file = openFileDialog.showOpenDialog(loadFile.getScene().getWindow());
        if (file != null) {
            filePath.setText(file.getAbsolutePath());
        }
    }

    private void sendFile() {
        Request request = new Request();
        File file = new File(filePath.getText());
        byte[] data = ControllerHelper.getDataFromFile(file);
        request.setParameter("type", MessageType.TASKSESSION);
        request.setParameter("taskFile", data);
        request.setParameter("taskFileSize", data.length);
        request.setParameter("taskName", taskName.getText());
        request.setParameter("taskFormatType", file.getName().substring(file.getName().indexOf(".")));
        requestListener.sendRequest(request);
    }

    public void setTaskQueue(List tasks) {
        taskQueueItems.clear();
        taskQueueItems.addAll(tasks);
        taskQueue.setItems(taskQueueItems);
    }

}
