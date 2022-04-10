package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.processors;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Topic;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.ImageMessage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

public class ImageMessageProcessor extends MessageProcessor {

    private static final Integer ROWS = 4;
    private static final Integer COLUMNS = 4;

    private final ArrayList<ImageMessage> aggregatedMessages = new ArrayList<>();
    private final HashMap<UUID, Integer> chunksPerImage = new HashMap<>();
    public ImageMessageProcessor(Topic topic) {
        super(topic);
    }

    @Override
    public void process(Message message) {
        ImageMessage imageMessage = (ImageMessage) message;
        aggregatedMessages.add(imageMessage);
        calculateChunksFor(imageMessage.getUuid());
        if(Objects.equals(chunksPerImage.get(imageMessage.getUuid()), imageMessage.getChunks())){
            BufferedImage image = (BufferedImage) imageMessage.getData();
            topic.addMessage(getCompleteImageMessage(imageMessage.getUuid(), imageMessage.getUsername(), image.getWidth(), image.getHeight(), image.getType()));
        }
    }

    private void calculateChunksFor(UUID messageUuid) {
        if(chunksPerImage.containsKey(messageUuid)) {
            chunksPerImage.put(messageUuid, chunksPerImage.get(messageUuid) + 1);
        } else {
            chunksPerImage.put(messageUuid, 1);
        }
    }

    private ImageMessage getCompleteImageMessage(UUID messageUuid, String username, Integer chunkWidth, Integer chunkHeight, Integer imageType) {
        ArrayList<BufferedImage> chunks = getChunks(messageUuid);
        BufferedImage completeImage = new BufferedImage(chunkWidth * COLUMNS, chunkHeight * ROWS, imageType);
        int chunkCounter = 0;
        Graphics2D graphics = completeImage.createGraphics();
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                graphics.drawImage(chunks.get(chunkCounter), i * chunkWidth, j * chunkHeight, null);
                chunkCounter++;
            }
        }
        return new ImageMessage(username, completeImage, null, null, messageUuid);
    }

    private ArrayList<BufferedImage> getChunks(UUID messageUuid) {
        ArrayList<ImageMessage> imageMessages = (ArrayList<ImageMessage>) aggregatedMessages
                .stream()
                .filter(it -> Objects.equals(it.getUuid(), messageUuid))
                .collect(Collectors.toList());
        imageMessages.sort(Comparator.comparing(ImageMessage::getChunk));
        return (ArrayList<BufferedImage>) imageMessages.stream().map(it -> (BufferedImage) it.getData()).collect(Collectors.toList());
    }
}
