/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.remotelauncher.shared.MessageType;
import com.remotelauncher.shared.Request;
import com.remotelauncher.tasks.ConnectionTask;
import com.remotelauncher.tasks.CreationInputStream;
import com.remotelauncher.tasks.CreationOutputStream;
import com.remotelauncher.tasks.RequestTask;
import com.remotelauncher.tasks.ServerListener;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private TCPClient tcpClient = new TCPClient();
    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private RequestTask requestTask;
    private ServerListener serverListener;
    private ResponseExecutor responseExecutor;

    private EditText username;
    private EditText password;
    private TextView loginStatusLabel;
    private ListView tasksQueue;
    private ArrayAdapter arrayAdapterForTasksQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        setContentView(R.layout.activity_main);
        tasksQueue = (ListView) findViewById(R.id.taskQueue);
        List<String> vals = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        arrayAdapterForTasksQueue = new ArrayAdapter(this, R.layout.listview_textview, R.id.textView3, vals);
        tasksQueue.setAdapter(arrayAdapterForTasksQueue);

        setContentView(R.layout.login_layout);

        Button connectButton = (Button) findViewById(R.id.connectButton);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginStatusLabel = (TextView) findViewById(R.id.loginStatusLabel);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLoginRequest();
            }
        });
    }

    private void init() {
        ConnectionTask connectionTask = new ConnectionTask();
        try {
            clientSocket = connectionTask.execute(tcpClient).get();
            CreationOutputStream creationOutputStream = new CreationOutputStream();
            outputStream = creationOutputStream.execute(clientSocket).get();
            CreationInputStream creationInputStream = new CreationInputStream();
            inputStream = creationInputStream.execute(clientSocket).get();
            requestTask = new RequestTask(clientSocket, outputStream);
            responseExecutor = new ResponseExecutor(this);
            serverListener = new ServerListener(clientSocket, inputStream, responseExecutor);
            serverListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
        request.setParameter(ClientConstants.USER_NAME, username.getText().toString());
        request.setParameter(ClientConstants.PASSWORD, password.getText().toString());
        new RequestTask(clientSocket, outputStream).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
    }

    public void setLoginStatusLabel(String message) {
        loginStatusLabel.setText(message);
    }

    public void openMainFrame() {
        setContentView(R.layout.activity_main);
        Request request = new Request();
        boolean showUncompletedTasks = false;
        boolean showMyTasksOnly = true;
        request.setParameter(ClientConstants.TYPE, MessageType.FILTERQUEUE);
        request.setParameter(ClientConstants.SHOW_UNCOMPLETED_TASKS, showUncompletedTasks);
        request.setParameter(ClientConstants.SHOW_MY_TASKS_ONLY, showMyTasksOnly);
        new RequestTask(clientSocket, outputStream).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
    }

    public void setTaskQueue(List<CellView> tasks) {
        setContentView(R.layout.activity_main);
        tasksQueue = (ListView) findViewById(R.id.taskQueue);
        tasksQueue.setAdapter(arrayAdapterForTasksQueue);
        List<String> values = new ArrayList<String>();
        for (CellView cellView : tasks) {
            values.add(cellView.getTaskName());
        }
        arrayAdapterForTasksQueue.clear();
        arrayAdapterForTasksQueue.addAll(values);
        arrayAdapterForTasksQueue.notifyDataSetChanged();
    }
}
