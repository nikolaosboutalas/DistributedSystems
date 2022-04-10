package master.handler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import common.Result;
import common.gpx.model.Waypoint;
import common.mapreduce.DataStore;

abstract public class Handler implements Runnable {

    protected Socket clientSocket;
    protected DataStore<ArrayList<Waypoint>, Result> dataStore;

    protected Handler(Socket clientSocket, DataStore<ArrayList<Waypoint>, Result> dataStore) {
        this.clientSocket = clientSocket;
        this.dataStore = dataStore;
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    abstract protected void handle() throws IOException, InterruptedException;

    abstract protected String type();
}
