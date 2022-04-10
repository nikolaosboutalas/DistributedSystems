package me.boutalas.distributedsystems.eventdeliverysystem.broker;

import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Broker  {

    public List<Message> messages = new ArrayList<Message>();

    public void listenTo(Integer port) {
        try {
            System.out.println("[INFO] Trying to listen on port: " + port);
            ServerSocket serverSocket = new ServerSocket(port);
            acceptConnectionsIndefinitely(serverSocket);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to listen on port: " + port);
            e.printStackTrace();
        }
    }

    private void acceptConnectionsIndefinitely(ServerSocket serverSocket) {
        while(true) acceptNewConnection(serverSocket);
    }

    private void acceptNewConnection(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            System.out.println("[INFO] New connection accepted: " + socket);
            handleConnection(socket);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to accept new connection");
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        System.out.println("[INFO] Starting new thread to handle the connection on " + socket);
        ListenThread listenThread = new ListenThread(this, socket);
        listenThread.start();
    }
}
