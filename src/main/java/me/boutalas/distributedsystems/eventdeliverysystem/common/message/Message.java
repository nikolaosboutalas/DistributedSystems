package me.boutalas.distributedsystems.eventdeliverysystem.common.message;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected MessageType messageType;
    private final String username;
    private final Object data;

    protected Message(String username, Object data) {
        this.username = username;
        this.data = data;
        setType();
    }

    protected abstract void setType();

    public enum MessageType {
        TEXT,
        IMAGE,
        VIDEO,
        UPDATE
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getUsername() {
        return username;
    }
    public Object getData() {
        return data;
    }
}
