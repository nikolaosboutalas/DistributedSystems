package me.boutalas.distributedsystems.eventdeliverysystem.broker;

import me.boutalas.distributedsystems.eventdeliverysystem.common.message.Message;

import java.io.*;
import java.net.Socket;

public class ListenThread extends Thread {

    private final Broker broker;
    private final Socket socket;

    public ListenThread(Broker broker, Socket socket) {
        this.broker = broker;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("[INFO] Trying to read message");
            Message message = readMessage(socket);
            broker.messages.add(message);
            System.out.println("[INFO] Successfully read message " + message);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ERROR] Failed to read message");
            e.printStackTrace();
        }
    }

    private Message readMessage(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        Message message = (Message) objectInputStream.readObject();
        socket.close();
        return message;
    }
}
