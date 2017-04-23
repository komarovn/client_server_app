package com.remotelauncher.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CreationOutputStream extends AsyncTask<Socket, Integer, ObjectOutputStream> {

    @Override
    protected void onPostExecute(ObjectOutputStream objectOutputStream) {
        super.onPostExecute(objectOutputStream);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected ObjectOutputStream doInBackground(Socket... params) {
        ObjectOutputStream result = null;

        try {
            result = new ObjectOutputStream(params[0].getOutputStream());
        } catch (IOException e) {
            System.out.print("Failed to create request thread");
        }

        return result;
    }
}
