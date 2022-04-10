package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.processors;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.VideoMessage;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class VideoMessageProcessor extends MessageProcessor {

    private final ArrayList<VideoMessage> aggregatedMessages = new ArrayList<>();
    private final HashMap<UUID, Integer> chunksPerVideo = new HashMap<>();

    public VideoMessageProcessor(Topic topic) {
        super(topic);
    }

    @Override
    public void process(Message message) {
        VideoMessage videoMessage = (VideoMessage) message;
        aggregatedMessages.add(videoMessage);
        calculateChunksFor(videoMessage.getUuid());
        if(Objects.equals(chunksPerVideo.get(videoMessage.getUuid()), videoMessage.getChunks())){
            topic.addMessage(getCompleteVideoMessage(videoMessage.getUuid(), videoMessage.getUsername()));
        }
    }

    private void calculateChunksFor(UUID messageUuid) {
        if(chunksPerVideo.containsKey(messageUuid)) {
            chunksPerVideo.put(messageUuid, chunksPerVideo.get(messageUuid) + 1);
        } else {
            chunksPerVideo.put(messageUuid, 1);
        }
    }

    private VideoMessage getCompleteVideoMessage(UUID messageUuid, String username) {
        ArrayList<byte[]> chunks = getChunks(messageUuid);
        int totalLength = 0;
        for(byte[] chunk: chunks) {
            totalLength += chunk.length;
        }
        ByteBuffer buff = ByteBuffer.wrap(new byte[totalLength]);
        for(byte[] chunk: chunks) {
            buff.put(chunk);
        }
        byte[] completeVideoInBytes = buff.array();
        return new VideoMessage(username, completeVideoInBytes, null, null, messageUuid);
    }

    private ArrayList<byte[]> getChunks(UUID messageUuid) {
        ArrayList<VideoMessage> videoMessages = (ArrayList<VideoMessage>) aggregatedMessages
                .stream()
                .filter(it -> Objects.equals(it.getUuid(), messageUuid))
                .collect(Collectors.toList());
        videoMessages.sort(Comparator.comparing(VideoMessage::getChunk));
        return (ArrayList<byte[]>) videoMessages.stream().map(it -> (byte[]) it.getData()).collect(Collectors.toList());
    }
}
