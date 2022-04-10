package master.handler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import common.ChunkWrapper;
import common.Result;
import common.ResultWrapper;
import common.gpx.model.Waypoint;
import common.mapreduce.DataStore;

public class WorkerHandler extends Handler {

    private UUID workerUUID;

    public WorkerHandler(Socket clientSocket, DataStore<ArrayList<Waypoint>, Result> dataStore, UUID workerUUID) {
        super(clientSocket, dataStore);
        this.workerUUID = workerUUID;
    }

    @Override
    protected void handle() throws IOException, InterruptedException {
        new Thread(new WriterThread(this.clientSocket, this.dataStore, this.workerUUID)).start();
        new Thread(new ReaderThread(this.clientSocket, this.dataStore)).start();
    }

    @Override
    protected String type() {
        return "WORKER";
    }

    private class WriterThread implements Runnable {

        private Socket clientSocket;
        private DataStore<ArrayList<Waypoint>, Result> dataStore;
        private UUID workerUUID;

        public WriterThread(Socket clientSocket, DataStore<ArrayList<Waypoint>, Result> dataStore, UUID workerUUID) {
            this.clientSocket = clientSocket;
            this.dataStore = dataStore;
            this.workerUUID = workerUUID;
        }

        @Override
        public void run() {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream())) {
                while (true) {
                    ArrayList<Integer> chunkIds;
                    try {
                        chunkIds = this.dataStore.getUnprocessedChunkIdsByWorkerUuid(workerUUID);
                        for (Integer chunkId : chunkIds) {
                            ArrayList<Waypoint> chunk = this.dataStore.getRawData(chunkId);
                            ChunkWrapper chunkWrapper = new ChunkWrapper(chunkId, chunk);
                            outputStream.writeObject(chunkWrapper);
                            this.dataStore.setIsSent(chunkId, true);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReaderThread implements Runnable {

        private Socket clientSocket;
        private DataStore<ArrayList<Waypoint>, Result> dataStore;

        public ReaderThread(Socket clientSocket, DataStore<ArrayList<Waypoint>, Result> dataStore) {
            this.clientSocket = clientSocket;
            this.dataStore = dataStore;
        }

        @Override
        public void run() {
                try (ObjectInputStream inputStream = new ObjectInputStream(this.clientSocket.getInputStream())) {
                    while (true) {
                        try {
                            ResultWrapper result;
                            result = (ResultWrapper) inputStream.readObject();
                            this.dataStore.setProcessedData(result.getChunkId(), result.getResult());
                        } catch (ClassNotFoundException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
