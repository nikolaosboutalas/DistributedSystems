package me.boutalas.distributedsystems;

import me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain.Broker;
import me.boutalas.distributedsystems.eventdeliverysystem.client.Client;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.ImageMessage;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;
import me.boutalas.distributedsystems.eventdeliverysystem.common.message.VideoMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class ClientInterface {
    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        String username = getString("Please enter your username: ", scanner);
        String host = getString("Please enter a message broker's IP address: ", scanner);
        Integer port = getInteger("Please enter the broker's port number: ", scanner);
        Client client = new Client(username, new Broker(host, port));
        loop:
        while (true) {
            System.out.println("Actions:\n" +
                    "1. Send text message\n" +
                    "2. Send video message\n" +
                    "3. Send image message\n" +
                    "4. Join topic\n" +
                    "5. Leave topic\n" +
                    "6. Show messages\n" +
                    "7. Terminate");
            Integer action = getInteger("Please choose action: ", scanner);
            String topicName;
            switch (action) {
                case 1:
                    sendText(scanner, client);
                    break;
                case 2:
                    sendVideo(scanner, client);
                    break;
                case 3:
                    sendImage(scanner, client);
                    break;
                case 4:
                    topicName = getString("Please enter the topic's name you wish to join: ", scanner);
                    client.subscribeTo(topicName);
                    break;
                case 5:
                    System.out.println("Please enter the topic's name you wish to leave: ");
                    topicName = chooseTopic(scanner, client);
                    client.unsubscribeFrom(topicName);
                    break;
                case 6:
                    topicName = chooseTopic(scanner, client);
                    ArrayList<Message> messages = client.getMessagesFor(topicName);
                    String pathToSaveFiles = getString("Please enter a path to save multimedia files: ", scanner);
                    for(Message message: messages) {
                        File file;
                        switch (message.getMessageType()) {
                            case TEXT:
                                System.out.println(message.getData());
                                break;
                            case IMAGE:
                                BufferedImage image = (BufferedImage) message.getData();
                                file = new File(pathToSaveFiles + ((ImageMessage) message).getUuid().toString());
                                ImageIO.write(image, "jpg", file);
                                System.out.println(pathToSaveFiles + ((ImageMessage) message).getUuid().toString());
                                break;
                            case VIDEO:
                                byte[] videoInBytes = (byte[]) message.getData();
                                FileOutputStream fileOutputStream = new FileOutputStream(pathToSaveFiles + ((VideoMessage) message).getUuid().toString());
                                fileOutputStream.write(videoInBytes);
                                fileOutputStream.close();
                                break;
                        }
                    }
                    break;
                case 7:
                    Set<String> topicNames = client.getTopicNames();
                    for (String it : topicNames) client.unsubscribeFrom(it);
                    break loop;
            }
        }
    }

    private static void sendImage(Scanner scanner, Client client) throws IOException {
        String topicName = chooseTopic(scanner, client);
        String imageFilePath = getString("Please enter the image file's path: ", scanner);
        BufferedImage image = ImageIO.read(new File(imageFilePath));
        client.sendImageTo(topicName, image);
    }

    private static void sendVideo(Scanner scanner, Client client) throws IOException {
        String topicName = chooseTopic(scanner, client);
        String videoFilePath = getString("Please enter the video file's path: ", scanner);
        File videoFile = new File(videoFilePath);
        FileInputStream fileInputStream = new FileInputStream(videoFile);
        byte[] videoInBytes = new byte[(int) videoFile.length()];
        fileInputStream.read(videoInBytes);
        client.sendVideoTo(topicName, videoInBytes);
    }

    private static void sendText(Scanner scanner, Client client) {
        String topicName = chooseTopic(scanner, client);
        String textInput = scanner.nextLine();
        client.sendTextTo(topicName, textInput);
    }

    private static String chooseTopic(Scanner scanner, Client client) {
        Set<String> topicNames;
        topicNames = client.getTopicNames();
        System.out.println("Choose topic: ");
        for (String it : topicNames) System.out.print(it + ", ");
        System.out.println();
        return getString("Please enter the topic's name: ", scanner);
    }

    private static Integer getInteger(String s, Scanner scanner) {
        System.out.print(s);
        return scanner.nextInt();
    }

    private static String getString(String s, Scanner scanner) {
        System.out.print(s);
        return scanner.next();
    }
}
