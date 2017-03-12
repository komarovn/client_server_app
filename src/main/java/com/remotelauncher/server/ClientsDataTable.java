/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import java.util.HashMap;

public class ClientsDataTable {

    private HashMap data;
    public static ClientsDataTable singleton;

    static {
        singleton = new ClientsDataTable();
    }

    private ClientsDataTable() {
        data = new HashMap();
    }

    public static void setData(String key, ClientData value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        singleton.data.put(key, value);
    }

    public static ClientData getData(String key) {
        return (ClientData)singleton.data.get(key);
        // TODO: Is it normal to cast object to ClientData? Delete that comment if it's ok
    }

}
