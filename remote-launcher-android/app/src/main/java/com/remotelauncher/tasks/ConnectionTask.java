package com.remotelauncher.tasks;

import android.os.AsyncTask;

import com.remotelauncher.TCPClient;

import java.net.Socket;

public class ConnectionTask extends AsyncTask<TCPClient, Integer, Socket> {

    @Override
    protected void onPostExecute(Socket s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Socket doInBackground(TCPClient... params) {
        params[0].runClient();
        return params[0].getClientSocket();
    }
}
