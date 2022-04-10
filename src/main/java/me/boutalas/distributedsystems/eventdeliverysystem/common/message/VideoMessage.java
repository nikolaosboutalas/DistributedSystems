package me.boutalas.distributedsystems.eventdeliverysystem.common.message;

import java.util.UUID;

public class VideoMessage extends Message {
    private final Integer chunks;
    private final Integer chunk;
    private final UUID uuid;

    public VideoMessage(String username, byte[] data, Integer chunks, Integer chunk, UUID uuid) {
        super(username, data);
        this.chunks = chunks;
        this.chunk = chunk;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Integer getChunks() {
        return chunks;
    }

    public Integer getChunk() {
        return chunk;
    }

    @Override
    protected void setType() {
        messageType = MessageType.VIDEO;
    }
}
