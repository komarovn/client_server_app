/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.shared;

import java.io.Serializable;
import java.util.HashMap;

public class Request implements Serializable {

    private HashMap<String, Object> data;

    public Request() {
        data = new HashMap<>();
    }

    public void setParameter(String key, Object value) {
        data.put(key, value);
    }

    public Object getParameter(String key) {
        return data.get(key);
    }

    @Override
    public String toString() {
        return "Request{" +
                "data=" + data +
                '}';
    }
}