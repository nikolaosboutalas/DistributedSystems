package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Broker;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Connection;
import me.boutalas.distributedsystems.eventdeliverysystem.client.Node;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Consumer extends Node {

    private final HashMap<Topic, ListenThread> listenThreads = new HashMap<>();

    public Consumer(String username, Broker broker) {
        super(username, broker);
    }

    @Override
    public void connectTo(Topic topic) throws IOException, NoSuchAlgorithmException {
        Broker broker = getBrokerFor(topic.getName());
        Connection connection = openConnection(broker);
        connections.put(topic, connection);
        startListening(connection, topic, username);
    }

    @Override
    public void disconnectFrom(Topic topic) throws IOException, InterruptedException {
        Connection connection = connections.remove(topic);
        stopListening(topic);
        closeConnection(connection);
    }

    private void startListening(Connection connection, Topic topic, String username) {
        ListenThread listenThread = new ListenThread(connection, topic, username);
        listenThread.start();
        listenThreads.put(topic, listenThread);
    }

    private void stopListening(Topic topic) throws InterruptedException {
        ListenThread listenThread = listenThreads.remove(topic);
        listenThread.interrupt();
        Thread.sleep(5000); // Wait for 5 seconds before closing socket
    }
}
