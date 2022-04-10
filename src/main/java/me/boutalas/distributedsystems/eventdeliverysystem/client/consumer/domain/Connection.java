package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain;

import java.io.IOException;
import java.net.Socket;

public class Connection {

    private final String host;
    private final Integer port;
    private final Socket socket;

    public Connection(String host, Integer port) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
