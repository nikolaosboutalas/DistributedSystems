package master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import common.Result;
import common.Statistics;
import common.gpx.model.Waypoint;
import common.mapreduce.DataStore;
import common.util.RoundRobinList;
import master.handler.ClientHandler;
import master.handler.WorkerHandler;
import master.handler.Handler;

enum ListenerType {
    WORKER, CLIENT;
}

public class Listener implements Runnable {
    private ListenerType listenerType;
    private Integer portNumber;
    private RoundRobinList<UUID> workers;
    private DataStore<ArrayList<Waypoint>, Result> dataStore;
    private Statistics statistics;

    public Listener(ListenerType worker, Integer workerListenerPortNumber,
            DataStore<ArrayList<Waypoint>, Result> dataStore, RoundRobinList<UUID> workers, Statistics statistics) {
        this.listenerType = worker;
        this.portNumber = workerListenerPortNumber;
        this.dataStore = dataStore;
        this.workers = workers;
        this.statistics = statistics;
    }

    @Override
    public void run() {
        try {
            try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    Handler handler = null;
                    switch (this.listenerType) {
                        case WORKER:
                            UUID workerUUID = UUID.randomUUID();
                            workers.add(workerUUID);
                            handler = new WorkerHandler(clientSocket, dataStore, workerUUID);
                            break;
                        case CLIENT:
                            handler = new ClientHandler(clientSocket, dataStore, statistics);
                            break;
                    }
                    new Thread(handler).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
