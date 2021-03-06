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
import com.remotelauncher.shared.TaskItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RemoteLauncherController implements Initializable {
    private final String CHOOSE_A_FILE = "Choose a File...";

    private RemoteLauncher mainApp;
    private String userId;
    private RequestListener requestListener;
    private ObservableList<CellView> taskQueueItems = FXCollections.observableArrayList();
    private ObservableList taskGroupsList = FXCollections.observableArrayList();
    private List<Task> tasksList = new ArrayList<>();
    private File filePathToDownload;

    @FXML
    private ListView<CellView> taskQueue;
    @FXML
    private ListView<CellView> taskGroups;

    /* Task Group */
    @FXML
    private Group taskGroup;
    @FXML
    private Button loadFile;
    @FXML
    private TextField filePath;
    @FXML
    private TextField taskName;
    @FXML
    private Button createTask;
    @FXML
    private Button addNewTaskGroup;

    /* Filter Queue */
    @FXML
    private RadioMenuItem uncompletedTasksOnly;
    @FXML
    private RadioMenuItem allTasks;
    @FXML
    private RadioMenuItem myTasksOnly;
    @FXML
    private RadioMenuItem allUsersTasks;

    /* Manage Task */
    @FXML
    private TextField taskNameManageTask;
    @FXML
    private Button downloadResultFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taskQueue.setItems(taskQueueItems);
        taskGroups.setItems(taskGroupsList);
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
                taskGroupsList.clear();
                tasksList.clear();
                validateCreateTask();
            }
        });
        initFilterQueue();
        addNewTaskGroup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tasksList.add(new Task(taskName.getText(), filePath.getText()));
                taskGroupsList.add(taskName.getText());
                filePath.setText(CHOOSE_A_FILE);
                taskName.setText("");
            }
        });
        initManageTask();
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
            addNewTaskGroup.setDisable(true);
        } else {
            addNewTaskGroup.setDisable(false);
        }
        if (taskGroupsList.isEmpty()) {
            createTask.setDisable(true);
        }
        else {
            createTask.setDisable(false);
        }
    }

    private void validateManageTask() {
        if (!taskNameManageTask.getText().isEmpty()) {
            boolean isOk = false;
            for (CellView task : taskQueueItems) {
                if (task.getTaskName().equals(taskNameManageTask.getText()) &&
                        task.getUserId().toString().equals(userId)) {
                    isOk = true;
                }
            }
            if (isOk) {
                downloadResultFile.setDisable(false);
            }
            else {
                downloadResultFile.setDisable(true);
            }
        }
        else {
            downloadResultFile.setDisable(true);
        }
    }

    private void sendFile() {
        Request request = new Request();
        request.setParameter(ClientConstants.TYPE, MessageType.TASKSESSION);
        List<TaskItem> taskItems = new ArrayList<>();
        for (Task task : tasksList) {
            File file = new File(task.path);
            byte[] data = ControllerHelper.getDataFromFile(file);
            taskItems.add(new TaskItem(task.name,
                    file.getName().substring(file.getName().indexOf(".")),
                    data.length,
                    data));
        }
        request.setParameter(ClientConstants.TASK_SESSION, taskItems);
        /*request.setParameter(ClientConstants.TASK_FILE, data);
        request.setParameter(ClientConstants.TASK_FILE_SIZE, data.length);
        request.setParameter(ClientConstants.TASK_NAME, taskName.getText());
        request.setParameter(ClientConstants.TASK_FORMAT_TYPE, );*/
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

    public void loadUserData() {
        sendFilterRequest();
    }

    private void initManageTask() {
        taskNameManageTask.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validateManageTask();
            }
        });
        taskQueue.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                CellView task = taskQueue.getSelectionModel().getSelectedItem();
                if (task.getUserId().toString().equals(userId)) {
                    taskNameManageTask.setText(task.getTaskName());
                }
                else {
                    taskNameManageTask.setText("");
                }
            }
        });
        downloadResultFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                filePathToDownload = null;
                FileChooser saveFileDialog = new FileChooser();
                filePathToDownload = saveFileDialog.showSaveDialog(downloadResultFile.getScene().getWindow());
                if (filePathToDownload != null) {
                    prepareDownloadResult(taskNameManageTask.getText());
                }
            }
        });
        validateManageTask();
    }

    private void prepareDownloadResult(String taskName) {
        Integer taskId = null;
        for (CellView task : taskQueueItems) {
            if (task.getTaskName().equals(taskName)) {
                taskId = task.getTaskId();
            }
        }
        if (taskId != null) {
            Request request = new Request();
            request.setParameter(ClientConstants.TYPE, MessageType.DLRESULT);
            request.setParameter(ClientConstants.TASK_ID, taskId);
            requestListener.sendRequest(request);
        }
    }

    public void downloadResult(byte[] data) {
        ControllerHelper.saveDataToFile(filePathToDownload, data);
    }

    private class Task {
        public String name;
        public String path;

        public Task(String name, String path) {
            this.name = name;
            this.path = path;
        }
    }
}
