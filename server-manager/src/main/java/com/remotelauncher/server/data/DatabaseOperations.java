/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server.data;

import java.sql.*;

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

    public void executeStatement(String query, Object ... param) {
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

    public void insertNewTask(Object task, String userId) {
        String query = "INSERT INTO remotelauncher.tasks (`task`, `is_completed`, `user_id`) VALUES(?, 0, ?)";
        executeStatement(query, task, userId);
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
