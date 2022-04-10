package me.boutalas.distributedsystems.eventdeliverysystem.client.producer;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Connection;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;

import java.io.*;
import java.net.Socket;

public class SendThread extends Thread {

    private final Connection connection;
    private final Message message;

    public SendThread(Connection connection, Message message) {
        this.connection = connection;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            writeMessage(connection, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeMessage(Connection connection, Message message) throws IOException {
        Socket socket = connection.getSocket();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(message);
    }
}
