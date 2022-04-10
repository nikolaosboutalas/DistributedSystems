package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.processors;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.UpdateMessage;

public class UpdateMessageProcessor extends MessageProcessor {
    public UpdateMessageProcessor(Topic topic) {
        super(topic);
    }

    @Override
    public void process(Message message) {
        UpdateMessage updateMessage = (UpdateMessage) message;
    }
}
