/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
}
