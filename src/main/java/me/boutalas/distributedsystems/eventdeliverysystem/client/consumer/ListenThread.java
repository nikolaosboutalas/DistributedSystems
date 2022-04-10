package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Connection;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.UpdateMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static me.boutalas.distributedsystems.eventdeliverysystem.common.message.UpdateMessage.UpdateType.REGISTER;
import static me.boutalas.distributedsystems.eventdeliverysystem.common.message.UpdateMessage.UpdateType.UNREGISTER;

public class ListenThread extends Thread {

    private final Connection connection;
    private final Topic topic;
    private final String username;
    private final MessageHandler messageHandler;

    public ListenThread(Connection connection, Topic topic, String username) {
        this.connection = connection;
        this.topic = topic;
        this.username = username;
        messageHandler = new MessageHandler(topic);
    }

    @Override
    public void run() {
        try {
            Socket socket = connection.getSocket();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            register(objectOutputStream);
            while(!Thread.currentThread().isInterrupted()) {
                if(objectInputStream.available() > 0) {
                    Message message = (Message) objectInputStream.readObject();
                    messageHandler.handle(message);
                }
            }
            unregister(objectOutputStream);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(ObjectOutputStream objectOutputStream) throws IOException {
        UpdateMessage registerMessage = new UpdateMessage(username, topic.getName(), REGISTER);
        objectOutputStream.writeObject(registerMessage);
    }

    private void unregister(ObjectOutputStream objectOutputStream) throws IOException {
        UpdateMessage unregisterMessage = new UpdateMessage(username, topic.getName(), UNREGISTER);
        objectOutputStream.writeObject(unregisterMessage);
    }
}
