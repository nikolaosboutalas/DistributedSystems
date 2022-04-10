package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.processors.*;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;

public class MessageHandler {

    private final Topic topic;
    private final VideoMessageProcessor videoMessageProcessor;
    private final ImageMessageProcessor imageMessageProcessor;

    public MessageHandler(Topic topic) {
        this.topic = topic;
        this.videoMessageProcessor = new VideoMessageProcessor(topic);
        this.imageMessageProcessor = new ImageMessageProcessor(topic);
    }
    public void handle(Message message) {
        MessageProcessor processor = null;
        switch (message.getMessageType()) {
            case UPDATE:
                processor = new UpdateMessageProcessor(topic);
                break;
            case TEXT:
                processor = new TextMessageProcessor(topic);
                break;
            case IMAGE:
                processor = imageMessageProcessor;
                break;
            case VIDEO:
                processor = videoMessageProcessor;
                break;
        }
        processor.process(message);
    }
}
