/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.data;

import com.remotelauncher.ServerConstants;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * API for execution of database operations.
 */
public class DatabaseOperations {
    private Connection connection;

    public DatabaseOperations() {
        createConnection();
    }

    private void createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/remotelauncher?useSSL=false",
                    "root",
                    "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeSingleQuery(String query) {
        ResultSet result = null;
        try {
            Statement statement = connection.createStatement();
            result = statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void executeStatement(String query, Object... param) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < param.length; i++) {
                statement.setObject(i + 1, param[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQueryWithParams(String query, Object... param) {
        ResultSet result = null;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < param.length; i++) {
                statement.setObject(i + 1, param[i]);
            }
            result = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isUserExists(String name) {
        if (name == null) throw new NullPointerException();
        String query = "SELECT * FROM remotelauncher.users WHERE `name` = '" + name + "'";
        ResultSet result = executeSingleQuery(query);
        try {
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createNewUser(String name, String password) {
        String query = "INSERT INTO remotelauncher.users (`name`, `password`) VALUES(?, ?)";
        executeStatement(query, name, password);
    }

    public String getUserIdByName(String name) {
        String userId = null;
        try {
            String query = "SELECT `user_id` FROM remotelauncher.users WHERE `name` = '" + name + "'";
            ResultSet result = executeSingleQuery(query);
            if (result.next()) {
                userId = String.valueOf(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    public boolean checkPasswordForUser(String name, String password) {
        try {
            String query = "SELECT `password` FROM remotelauncher.users WHERE `name` = '" + name + "'";
            ResultSet result = executeSingleQuery(query);
            if (result.next() && result.getString(1).equals(password)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String insertNewTask(byte[] task, String name, String userId, String format) {
        String taskId = String.valueOf(Math.abs(UUID.randomUUID().hashCode()));
        try {
            Blob taskBlob = new SerialBlob(task);
            String query = "INSERT INTO remotelauncher.tasks (`task_id`, `name`, `task`, `is_completed`, `user_id`, " +
                    "`format_type`) VALUES(?, ?, ?, 0, ?, ?)";
            executeStatement(query, taskId, name, taskBlob, userId, format);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskId;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTaskIsCompleted(String taskId, byte[] output) {
        String outputId = String.valueOf(Math.abs(UUID.randomUUID().hashCode()));
        String query = "INSERT INTO remotelauncher.output (`output_id`, `file`) VALUES(?, ?)";
        try {
            Blob blob = new SerialBlob(output);
            executeStatement(query, outputId, blob);
            query = "UPDATE remotelauncher.tasks SET is_completed=1,output_id=? WHERE task_id=?";
            executeStatement(query, outputId, taskId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public List<HashMap<String, Object>> getQueueUpdateOfUndoneTaskSessions() {
        List<HashMap<String, Object>> result = new ArrayList<>();
        String query = "SELECT * FROM remotelauncher.tasks WHERE is_completed=0";
        ResultSet resultSet =  executeSingleQuery(query);
        try {
            while(resultSet.next()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(ServerConstants.TASK_ID, resultSet.getInt(1));
                hashMap.put(ServerConstants.TASK_NAME, resultSet.getString(2));
                hashMap.put(ServerConstants.TASK_USER_ID, resultSet.getInt(6));
                result.add(hashMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<HashMap<String, Object>> getQueueUpdateOfTaskSessions(boolean isCompleted, String userId) {
        List<HashMap<String, Object>> result = new ArrayList<>();
        String query = "SELECT * FROM remotelauncher.tasks";
        ResultSet resultSet;
        if (userId != null) {
            query += " WHERE user_id = ?";
            if (isCompleted) {
                query += " and is_completed = ?";
                resultSet = executeQueryWithParams(query, userId, !isCompleted);
            }
            else {
                resultSet = executeQueryWithParams(query, userId);
            }
        }
        else if (isCompleted) {
            query += " WHERE is_completed = ?";
            resultSet = executeQueryWithParams(query, !isCompleted);
        }

        try {
            while(resultSet.next()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(ServerConstants.TASK_ID, resultSet.getInt(1));
                hashMap.put(ServerConstants.TASK_NAME, resultSet.getString(2));
                hashMap.put(ServerConstants.TASK_IS_COMPLETED, resultSet.getBoolean(4));
                hashMap.put(ServerConstants.TASK_USER_ID, resultSet.getInt(6));
                result.add(hashMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Connection getConnection() {
        return connection;
    }

}
