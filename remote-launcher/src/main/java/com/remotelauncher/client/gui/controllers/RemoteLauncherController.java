/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui.controllers;

import com.remotelauncher.client.listeners.RequestListener;
import com.remotelauncher.client.listeners.ResponseListener;
import com.remotelauncher.client.gui.RemoteLauncher;
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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RemoteLauncherController implements Initializable, ResponseListener {
    private final String CHOOSE_A_FILE = "Choose a File...";

    private RemoteLauncher mainApp;
    private RequestListener requestListener;
    private ObservableList taskQueueItems;

    @FXML
    private Button loadFile;

    @FXML
    private TextField filePath;

    @FXML
    private Button createTask;

    @FXML
    private ListView taskQueue;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taskQueueItems = FXCollections.observableArrayList("One", "Two", "Three", "Four");
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
                if (filePath.getText().equals(CHOOSE_A_FILE)) {
                    createTask.setDisable(true);
                }
                else {
                    createTask.setDisable(false);
                }
            }
        });
        createTask.setDisable(true);
        createTask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String filePathValue = filePath.getText();
                taskQueueItems.add(filePathValue);
                taskQueue.setItems(taskQueueItems);
                filePath.setText(CHOOSE_A_FILE);
            }
        });
    }

    @Override
    public void receiveResponse(Response response) {

    }

    public void setMainApp(RemoteLauncher mainApp) {
        this.mainApp = mainApp;
    }

    public void addRequestListener(RequestListener listener) {
        requestListener = listener;
    }

    private void openChooseFileDialog() {
        FileChooser openFileDialog = new FileChooser();
        File file = openFileDialog.showOpenDialog(loadFile.getScene().getWindow());
        if (file != null) {
            filePath.setText(file.getAbsolutePath());
        }
    }

}
