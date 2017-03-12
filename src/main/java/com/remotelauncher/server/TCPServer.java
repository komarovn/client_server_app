/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher.server;

import com.remotelauncher.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class TCPServer {

    public void runServer() {
        ServerSocket serverSocket = null;
        System.out.println("SERVER IS STARTING...");
        try {
            serverSocket = new ServerSocket(Constants.PORT_NUMBER);
            System.out.println("SERVER HAS STARTED.");
        } catch (IOException ex) {
            System.out.println("SERVER START HAS FAILED!");
            ex.printStackTrace();
            System.exit(0);
        }
        ClientsData clientsData = ClientsData.singleton;
        System.out.printf("====\nLISTENING FOR PORT %d...\n", Constants.PORT_NUMBER);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();

                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

                String token = dataInputStream.readUTF();
                System.out.printf("CLIENT WITH TOKEN %s WAS CONNECTED.\n", token);
                Integer userId = token.hashCode();
                Object clientData = ClientsData.getData(userId.toString());
                if (clientData == null) {
                    WorkThread workThread = new WorkThread();
                    long workThreadID = workThread.getId();
                    System.out.printf("CLIENT HAS NOT BEEN REGISTRED IN THE SYSTEM. HIS NEW WORK THREAD ID: %s \n", workThreadID);
                    ClientsData.setData(userId.toString(), workThreadID);
                }
                else {
                    long workThreadID = (long) clientData;
                    System.out.printf("CLIENT HAS ALREADY BEEN REGISTRED IN THE SYSTEM. HIS NEW WORK THREAD ID: %s \n", workThreadID);
                    // TODO: send queue status to client
                }
                System.out.println("----\nLOOKING FOR NEW CLIENTS...");

                /*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String GET = bufferedReader.readLine();
                System.out.println(GET);
                while (true) {
                    String line = bufferedReader.readLine();
                    System.out.println(line);
                    if (line == null || line.trim().length() == 0) {
                        break;
                    }
                }
                System.out.println("read loop exit");
                if (GET != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(GET);
                    stringTokenizer.nextToken();
                    String resource = stringTokenizer.nextToken();
                    System.out.printf("resoure=%s\n\n", resource);

                    StringBuilder response = new StringBuilder();

                    SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
                    response.append("HTTP/1.1 200 OK\r\n");
                    response.append("Server: MyWebServer\r\n");
                    response.append("Content-Type: application/java\r\n");
                    response.append("Date: "); response.append(df.format(new Date())); response.append("\r\n");

                    BufferedInputStream is = new BufferedInputStream(new FileInputStream("./" + resource));
                    ByteArrayOutputStream aos = new ByteArrayOutputStream();
                    int data;
                    int len = 0;
                    while ((data = is.read()) != -1) {
                        aos.write(data);
                        len++;
                    }
                    is.close();

                    response.append("Content-Length: ");response.append(len);response.append("\r\n");
                    response.append("Connection: close\r\n");
                    response.append("\r\n");

                    OutputStream os = clientSocket.getOutputStream();
                    os.write(response.toString().getBytes());
                    os.write(aos.toByteArray());
                    os.close();

                }*/

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void processResponse() {

    }

}
