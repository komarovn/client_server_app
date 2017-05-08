package com.remotelauncher.tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class CreationInputStream extends AsyncTask<Socket, Integer, ObjectInputStream> {

    @Override
    protected void onPostExecute(ObjectInputStream objectInputStream) {
        super.onPostExecute(objectInputStream);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected ObjectInputStream doInBackground(Socket... params) {
        ObjectInputStream result = null;

        try {
            result = new ObjectInputStream(params[0].getInputStream());
        } catch (IOException e) {
            System.out.print("Failed to create input stream");
        }

        return result;
    }
}
