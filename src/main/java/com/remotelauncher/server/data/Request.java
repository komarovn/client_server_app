package com.remotelauncher.server.data;

import java.util.HashMap;

/**
 * Created by rpovelik on 19/03/2017.
 */
public class Request {
    private HashMap<String,Object> data;

    public Request(){
       data = new HashMap<>();
    }

    public void setParameter(String key, Object value){
        data.put(key,value);
    }

    public Object getParameter(String key){
        return data.get(key);
    }
}
