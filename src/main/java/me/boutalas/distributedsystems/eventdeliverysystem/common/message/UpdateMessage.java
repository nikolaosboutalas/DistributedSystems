package me.boutalas.distributedsystems.eventdeliverysystem.common.message;

public class UpdateMessage extends Message {

    private final UpdateType updateType;

    public UpdateMessage(String username, String data, UpdateType updateType) {
        super(username, data);
        this.updateType = updateType;
    }

    @Override
    protected void setType() {
        messageType = MessageType.UPDATE;
    }

    public enum UpdateType {
        REGISTER,
        UNREGISTER
    }
}
