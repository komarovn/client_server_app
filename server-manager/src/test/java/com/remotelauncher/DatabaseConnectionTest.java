/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class DatabaseConnectionTest {
    Connection connection;

    private void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/remotelauncher?useSSL=false",
                    "root",
                    "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute() {
        try {
            Statement statement = connection.createStatement();
            ScriptRunner scriptRunner = new ScriptRunner(connection, false, true);
            scriptRunner.runScript(new BufferedReader(new FileReader(getClass().getResource("/sql/data.sql").getFile())));
            ResultSet result = statement.executeQuery("SELECT * FROM remotelauncher.users;");
            while (result.next()) {
                System.out.println(result.getInt(1) + " | " +
                        result.getString(2) + " | " +
                        result.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void test() {
        connect();
        execute();
        closeConnection();
    }

    public static void main(String[] args) {
        DatabaseConnectionTest databaseConnectionTest = new DatabaseConnectionTest();
        databaseConnectionTest.test();
    }
}
