package me.boutalas.distributedsystems;

import me.boutalas.distributedsystems.eventdeliverysystem.broker.Broker;

import java.util.Scanner;

public class BrokerInterface {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Integer portNumber = getPortNumber(scanner);
        Broker broker = new Broker();
        broker.listenTo(portNumber);
    }

    private static Integer getPortNumber(Scanner scanner) {
        System.out.print("Please choose a port number: ");
        return scanner.nextInt();
    }
}
