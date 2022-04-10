package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.processors;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.TextMessage;

public class TextMessageProcessor extends MessageProcessor {
    public TextMessageProcessor(Topic topic) {
        super(topic);
    }

    @Override
    public void process(Message message) {
        TextMessage textMessage = (TextMessage) message;
        topic.addMessage(message);
    }
}
