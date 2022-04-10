package me.boutalas.distributedsystems.eventdeliverysystem.client.producer;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Broker;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Connection;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;
import me.boutalas.distributedsystems.eventdeliverysystem.client.Node;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.UpdateMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static me.boutalas.distributedsystems.eventdeliverysystem.common.message.UpdateMessage.UpdateType.REGISTER;
import static me.boutalas.distributedsystems.eventdeliverysystem.common.message.UpdateMessage.UpdateType.UNREGISTER;

public class Producer extends Node {

    public Producer(String username, Broker broker) {
        super(username, broker);
    }

    @Override
    public void connectTo(Topic topic) throws IOException, NoSuchAlgorithmException {
        Broker broker = getBrokerFor(topic.getName());
        Connection connection = openConnection(broker);
        UpdateMessage registerMessage = new UpdateMessage(username, topic.getName(), REGISTER);
        new SendThread(connection, registerMessage).start();
        connections.put(topic, connection);
    }

    @Override
    public void disconnectFrom(Topic topic) throws IOException, InterruptedException {
        Connection connection = connections.remove(topic);
        UpdateMessage unregisterMessage = new UpdateMessage(username, topic.getName(), UNREGISTER);
        new SendThread(connection, unregisterMessage);
        Thread.sleep(5000);
        closeConnection(connection);
    }

    public void push(Topic topic, Message message) {
        Connection connection = connections.get(topic);
        SendThread sendThread = new SendThread(connection, message);
        sendThread.start();
    }

}
