package master;
import static master.ListenerType.CLIENT;
import static master.ListenerType.WORKER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import common.Result;
import common.Statistics;
import common.gpx.model.Waypoint;
import common.mapreduce.DataStore;
import common.util.RoundRobinList;

class Input {
    public static Integer getPortNumber(Scanner scanner) {
        return scanner.nextInt();
    }
}

public class Master {
    public static void main(String[] args) throws IOException {
        RoundRobinList<UUID> workers = new RoundRobinList<>();
        DataStore<ArrayList<Waypoint>, Result> dataStore = new DataStore<>(workers);
        Statistics statistics = new Statistics();
        Scanner scanner = new Scanner(System.in);
        System.out.println("[MASTER] Enter worker listener port number: ");
        Integer workerListenerPortNumber = Input.getPortNumber(scanner);
        System.out.println("[MASTER] Enter client listener port number: ");
        Integer clientListenerPortNumber = Input.getPortNumber(scanner);
        new Thread(new Listener(WORKER, workerListenerPortNumber, dataStore, workers, statistics)).start();
        new Thread(new Listener(CLIENT, clientListenerPortNumber, dataStore, workers, statistics)).start();
    }
}







