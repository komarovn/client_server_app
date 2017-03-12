/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import java.util.HashMap;

public class ClientsData {

    private HashMap data;
    public static ClientsData singleton;

    static {
        singleton = new ClientsData();
    }

    private ClientsData() {
        data = new HashMap();
    }

    public static void setData(String key, Object value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        singleton.data.put(key, value);
    }

    public static Object getData(String key) {
        return singleton.data.get(key);
    }

}
