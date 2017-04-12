/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client.gui.controllers;

import com.remotelauncher.ClientConstants;
import com.remotelauncher.client.ControllerHelper;
import com.remotelauncher.client.listeners.RequestListener;
import com.remotelauncher.client.gui.RemoteLauncher;
import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RemoteLauncherController implements Initializable {
    private final String CHOOSE_A_FILE = "Choose a File...";

    private RemoteLauncher mainApp;
    private String userId;
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
    private ListView<CellView> taskQueue;

    /* Filter Queue */
    @FXML
    private RadioMenuItem uncompletedTasksOnly;
    @FXML
    private RadioMenuItem allTasks;
    @FXML
    private RadioMenuItem myTasksOnly;
    @FXML
    private RadioMenuItem allUsersTasks;

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
                validateCreateTask();
            }
        });
        taskName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validateCreateTask();
            }
        });
        createTask.setDisable(true);
        createTask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendFile();
                filePath.setText(CHOOSE_A_FILE);
                taskName.setText("");
            }
        });
        initFilterQueue();
    }

    public void setMainApp(RemoteLauncher mainApp) {
        this.mainApp = mainApp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addRequestListener(RequestListener listener) {
        requestListener = listener;
    }

    private void initFilterQueue() {
        uncompletedTasksOnly.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendFilterRequest();
            }
        });
        allTasks.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendFilterRequest();
            }
        });
        myTasksOnly.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendFilterRequest();
            }
        });
        allUsersTasks.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendFilterRequest();
            }
        });
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

    private void validateCreateTask() {
        if (filePath.getText().equals(CHOOSE_A_FILE) || taskName.getText().isEmpty()) {
            createTask.setDisable(true);
        } else {
            createTask.setDisable(false);
        }
    }

    private void sendFile() {
        Request request = new Request();
        File file = new File(filePath.getText());
        byte[] data = ControllerHelper.getDataFromFile(file);
        request.setParameter(ClientConstants.TYPE, MessageType.TASKSESSION);
        request.setParameter(ClientConstants.TASK_FILE, data);
        request.setParameter(ClientConstants.TASK_FILE_SIZE, data.length);
        request.setParameter(ClientConstants.TASK_NAME, taskName.getText());
        request.setParameter(ClientConstants.TASK_FORMAT_TYPE, file.getName().substring(file.getName().indexOf(".")));
        requestListener.sendRequest(request);
    }

    public void setTaskQueue(List<CellView> tasks) {
        taskQueueItems.clear();
        taskQueueItems.addAll(tasks);
        taskQueue.setCellFactory(new Callback<ListView<CellView>, ListCell<CellView>>() {
            @Override
            public ListCell<CellView> call(ListView<CellView> param) {
                return new ListCell<CellView>() {
                    @Override
                    protected void updateItem(CellView item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            Text taskName = new Text(item.getTaskName());
                            if (!item.getUserId().toString().equals(userId)) {
                                taskName.setFill(Color.GRAY);
                            }
                            else {
                                taskName.setStyle("-fx-font-weight: bold");
                            }
                            HBox horizontalBox = new HBox(taskName);
                            horizontalBox.setSpacing(10);
                            setGraphic(horizontalBox);
                        }
                    }
                };
            }
        });
    }

    private void sendFilterRequest() {
        boolean showUncompletedTasks = true;
        boolean showMyTasksOnly = true;
        if (uncompletedTasksOnly.isSelected()) {
            showUncompletedTasks = true;
        } else if (allTasks.isSelected()) {
            showUncompletedTasks = false;
        }
        if (myTasksOnly.isSelected()) {
            showMyTasksOnly = true;
        } else if (allUsersTasks.isSelected()) {
            showMyTasksOnly = false;
        }
        Request request = new Request();
        request.setParameter(ClientConstants.TYPE, MessageType.FILTERQUEUE);
        request.setParameter(ClientConstants.SHOW_UNCOMPLETED_TASKS, showUncompletedTasks);
        request.setParameter(ClientConstants.SHOW_MY_TASKS_ONLY, showMyTasksOnly);
        requestListener.sendRequest(request);
    }

}
