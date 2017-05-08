package com.remotelauncher.tasks;

import android.os.AsyncTask;

import com.remotelauncher.ResponseExecutor;
import com.remotelauncher.shared.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerListener extends AsyncTask<Void, Integer, Response> {
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ResponseExecutor responseExecutor;

    public ServerListener(Socket clientSocket, ObjectInputStream inputStream, ResponseExecutor responseExecutor) {
        this.clientSocket = clientSocket;
        this.inputStream = inputStream;
        this.responseExecutor = responseExecutor;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Response doInBackground(Void... params) {
        while (!clientSocket.isClosed()) {
            Response response = receiveResponse();
            if (response != null) {
                responseExecutor.receiveResponse(response);
            }
        }
        return null;
    }

    private Response receiveResponse() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                return (Response) inputStream.readObject();
            }
        } catch (Exception e) {
            try {
                clientSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
