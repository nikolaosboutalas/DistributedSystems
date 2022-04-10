package me.boutalas.distributedsystems.eventdeliverysystem.client;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Broker;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Connection;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public abstract class Node {

    protected final HashMap<Topic, Connection> connections = new HashMap<>();
    private final HashSet<Broker> brokers = new HashSet<>();
    protected String username;

    public Node(String username, Broker broker) {
        this.username = username;
        brokers.add(broker);
    }

    public abstract void connectTo(Topic topic) throws IOException, NoSuchAlgorithmException;

    public abstract void disconnectFrom(Topic topic) throws IOException, InterruptedException;

    protected Connection openConnection(Broker broker) throws IOException {
        return new Connection(broker.getHost(), broker.getPort());
    }

    protected void closeConnection(Connection connection) throws IOException {
        connection.getSocket().close();
    }

    protected Broker getBrokerFor(String topicName) throws NoSuchAlgorithmException {
        String topicNameHash = getHashStringFor(topicName);
        for (Broker broker: brokers) {
            String brokerHash = getHashStringFor(broker.getHost() + broker.getPort());
            if (topicNameHash.compareTo(brokerHash) < 0) return broker;
        }
        return brokers.stream().findFirst().get();
    }

    private String getHashStringFor(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(str.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashText = no.toString(16);
        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }
        return hashText;
    }
}
