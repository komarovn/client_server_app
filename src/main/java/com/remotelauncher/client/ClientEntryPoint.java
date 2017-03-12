package com.remotelauncher.client;

/**
 * Created by User on 12.03.2017.
 */
public class ClientEntryPoint {

    public static void main(String[] args) {
        TCPClient tcpClient = new TCPClient();
        tcpClient.runClient();
    }

}
