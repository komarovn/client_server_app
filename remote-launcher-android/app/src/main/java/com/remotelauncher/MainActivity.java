/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.tasks.ConnectionTask;
import com.remotelauncher.tasks.CreationOutputStream;
import com.remotelauncher.tasks.LoginRequestTask;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private TCPClient tcpClient = new TCPClient();
    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private LoginRequestTask loginRequestTask;

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ConnectionTask connectionTask = new ConnectionTask();
        try {
            clientSocket = connectionTask.execute(tcpClient).get();
            CreationOutputStream creationOutputStream = new CreationOutputStream();
            outputStream = creationOutputStream.execute(clientSocket).get();
            loginRequestTask = new LoginRequestTask(clientSocket, outputStream);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Button connectButton = (Button) findViewById(R.id.connectButton);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_main);
                sendLoginRequest();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendLoginRequest() {
        Request request = new Request();
        request.setParameter(ClientConstants.TYPE, MessageType.LOGIN);
        request.setParameter(ClientConstants.USER_NAME, username.getText());
        request.setParameter(ClientConstants.PASSWORD, password.getText());
        loginRequestTask.execute(request);
    }
}
