package me.boutalas.distributedsystems.eventdeliverysystem.common.message;

public class TextMessage extends Message{
    public TextMessage(String username, String data) {
        super(username, data);
    }

    @Override
    protected void setType() {
        messageType = MessageType.TEXT;
    }
}
