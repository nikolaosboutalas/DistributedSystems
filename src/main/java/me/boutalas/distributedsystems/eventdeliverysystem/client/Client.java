package me.boutalas.distributedsystems.eventdeliverysystem.client;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.Consumer;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Broker;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.ImageMessage;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.TextMessage;
import me.boutalas.distributedsystems.eventdeliverysystem.client.producer.Producer;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.VideoMessage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static me.boutalas.distributedsystems.eventdeliverysystem.client.MessageComposer.composeMessageFor;

public class Client {

    private final String username;
    private final HashSet<Topic> topics = new HashSet<>();
    private final Producer producer;
    private final Consumer consumer;

    public Client(String username, Broker broker) {
        this.username = username;
        producer = new Producer(username, broker);
        consumer = new Consumer(username, broker);
    }

    public void subscribeTo(String topicName) throws IOException, NoSuchAlgorithmException {
        Topic topic = new Topic(topicName);
        topics.add(topic);
        producer.connectTo(topic);
        consumer.connectTo(topic);
    }

    public void unsubscribeFrom(String topicName) throws IOException, InterruptedException {
        Topic topic = getTopicBy(topicName);
        topics.remove(topic);
        producer.disconnectFrom(topic);
        consumer.disconnectFrom(topic);
    }

    public Set<String> getTopicNames() {
        return topics.stream().map(Topic::getName).collect(Collectors.toSet());
    }

    public void sendTextTo(String topicName, String text) {
        Topic topic = getTopicBy(topicName);
        TextMessage textMessage = composeMessageFor(username, text);
        producer.push(topic, textMessage);
    }

    public void sendImageTo(String topicName, BufferedImage image) {
        Topic topic = getTopicBy(topicName);
        ArrayList<ImageMessage> imageMessages = composeMessageFor(username, image);
        for(ImageMessage imageMessage: imageMessages) producer.push(topic, imageMessage);
    }

    public void sendVideoTo(String topicName, byte[] videoInBytes) {
        Topic topic = getTopicBy(topicName);
        ArrayList<VideoMessage> videoMessages = composeMessageFor(username, videoInBytes);
        for(VideoMessage videoMessage: videoMessages) producer.push(topic, videoMessage);
    }

    public ArrayList<Message> getMessagesFor(String topicName) {
        Topic topic = getTopicBy(topicName);
        return topic.getMessages();
    }

    private Topic getTopicBy(String topicName) {
        return topics.stream().filter(it -> Objects.equals(it.getName(), topicName)).findFirst().get();
    }
}
