package com.remotelauncher.client;

import com.remotelauncher.shared.Response;

public interface ResponseListener {

    void receiveResponse(Response response);
}
