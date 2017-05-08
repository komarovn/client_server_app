package com.remotelauncher.tasks;

import android.os.AsyncTask;

import com.remotelauncher.shared.Request;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestTask extends AsyncTask<Request, Integer, String>{
    private Socket clientSocket;
    private ObjectOutputStream outputStream;

    public RequestTask(Socket socket, ObjectOutputStream outputStream) {
        this.clientSocket = socket;
        this.outputStream = outputStream;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(Request... requests) {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                outputStream.writeObject(requests[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
