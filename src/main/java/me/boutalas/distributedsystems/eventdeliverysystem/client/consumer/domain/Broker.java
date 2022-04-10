package me.boutalas.distributedsystems.eventdeliverysystem.client.consumer.domain;

public class Broker {
    private final String host;
    private final Integer port;

    public Broker(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
