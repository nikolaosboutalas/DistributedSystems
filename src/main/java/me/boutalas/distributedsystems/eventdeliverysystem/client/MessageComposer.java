package me.boutalas.distributedsystems.eventdeliverysystem.client;

import me.boutalas.distributedsystems.eventdeliverysystem.common.message.ImageMessage;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.TextMessage;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.VideoMessage;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

import static java.util.Arrays.copyOfRange;

public class MessageComposer {

    private static final Integer ROWS = 4;
    private static final Integer COLUMNS = 4;
    private static final Integer BYTES = 1024;

    public static TextMessage composeMessageFor(String username, String text) {
        return new TextMessage(username, text);
    }

    public static ArrayList<ImageMessage> composeMessageFor(String username, BufferedImage image) {
        UUID uuid = UUID.randomUUID();
        ArrayList<BufferedImage> chunks = chunksFrom(image);
        ArrayList<ImageMessage> imageMessages = new ArrayList<>();
        Integer iteration = 1;
        for (BufferedImage chunk : chunks) {
            imageMessages.add(new ImageMessage(username, chunk, chunks.size(), iteration, uuid));
            iteration++;
        }
        return imageMessages;
    }

    public static ArrayList<VideoMessage> composeMessageFor(String username, byte[] videoInBytes) {
        UUID uuid = UUID.randomUUID();
        ArrayList<byte[]> chunks = chunksFrom(videoInBytes);
        ArrayList<VideoMessage> videoMessages = new ArrayList<>();
        Integer iteration = 1;
        for (byte[] chunk : chunks) {
            videoMessages.add(new VideoMessage(username, chunk, chunks.size(), iteration, uuid));
            iteration++;
        }
        return videoMessages;
    }

    private static ArrayList<BufferedImage> chunksFrom(BufferedImage image) {
        ArrayList<BufferedImage> chunks = new ArrayList<>();
        int chunkWidth = image.getWidth() / COLUMNS;
        int chunkHeight = image.getHeight() / ROWS;
        int x = 0;
        int y = 0;
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                chunks.add(image.getSubimage(x, y, chunkWidth, chunkHeight));
                x += chunkWidth;
            }
            y += chunkHeight;
        }
        return chunks;
    }

    private static ArrayList<byte[]> chunksFrom(byte[] videoInBytes) {
        ArrayList<byte[]> chunks = new ArrayList<>();
        int index = 0;
        while (index < videoInBytes.length) {
            if (videoInBytes.length - index >= BYTES)
                chunks.add(copyOfRange(videoInBytes, index, index + BYTES));
            else chunks.add(copyOfRange(videoInBytes, index, videoInBytes.length));
            index += BYTES;
        }
        return chunks;
    }
}
