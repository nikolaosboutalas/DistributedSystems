package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain;

import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;

import java.util.ArrayList;

public class Topic {
    private final String name;
    private final ArrayList<Message> messages = new ArrayList<Message>();

    public Topic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }
}
