/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import java.io.*;

public class ControllerHelper {

    public static byte[] getDataFromFile(File file) {
        byte[] data = new byte[(int) file.length()];
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            inputStream.read(data, 0, data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void saveDataToFile(File file, byte[] data) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                bufferedOutputStream.write(data);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
