package common.mapreduce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import common.util.RoundRobinList;

public class DataStore<RawData, ProcessedData> {

    private Map<Integer, DataEntry<RawData, ProcessedData>> dataEntries;
    private Integer idCounter;
    private RoundRobinList<UUID> workers;

    public DataStore(RoundRobinList<UUID> workers) {
        dataEntries = new HashMap<>();
        idCounter = 0;
        this.workers = workers;
    }

    public synchronized int add(RawData rawData) {
        Integer id = idCounter++;
        UUID workerUUID = workers.getNext();
        DataEntry<RawData, ProcessedData> dataEntry = new DataEntry<>(rawData, workerUUID);
        dataEntries.put(id, dataEntry);
        notifyAll();
        return id;
    }

    public synchronized RawData getRawData(int id) {
        return dataEntries.get(id).getRawData();
    }

    public synchronized ProcessedData getProcessedData(int id) throws InterruptedException {
        ProcessedData data = dataEntries.get(id).getProcessedData();
        while (data == null) {
            wait();
            data = dataEntries.get(id).getProcessedData();
        }
        return data;
    }

    public synchronized ArrayList<Integer> getUnprocessedChunkIdsByWorkerUuid(UUID workerUUID)
            throws InterruptedException {
        ArrayList<Integer> chunkIds = new ArrayList<Integer>(
                dataEntries
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue().getWorkerUUID().equals(workerUUID) &&
                                entry.getValue().getProcessedData() == null &&
                                entry.getValue().getIsSent() == false)
                        .map(entry -> entry.getKey())
                        .collect(Collectors.toList()));
        while (chunkIds.isEmpty()) {
            wait();
            chunkIds = new ArrayList<Integer>(
                    dataEntries
                            .entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().getWorkerUUID().equals(workerUUID) &&
                                    entry.getValue().getProcessedData() == null &&
                                    entry.getValue().getIsSent() == false)
                            .map(entry -> entry.getKey())
                            .collect(Collectors.toList()));
        }
        return chunkIds;
    }

    public synchronized void setIsSent(int id, Boolean isSent) {
        DataEntry<RawData, ProcessedData> entry = dataEntries.get(id);
        entry.setIsSent(isSent);
        notifyAll();
    }

    public synchronized void setProcessedData(int id, ProcessedData processedData) {
        DataEntry<RawData, ProcessedData> entry = dataEntries.get(id);
        entry.setProcessedData(processedData);
        notifyAll();
    }

    private static class DataEntry<Raw, Processed> {
        private final Raw rawData;
        private Processed processedData;
        private final UUID workerUUID;
        private Boolean isSent;

        public DataEntry(Raw rawData, UUID workerUUID) {
            this.rawData = rawData;
            this.processedData = null;
            this.workerUUID = workerUUID;
            this.isSent = false;
        }

        public Raw getRawData() {
            return rawData;
        }

        public Processed getProcessedData() {
            return processedData;
        }

        public void setProcessedData(Processed processedData) {
            this.processedData = processedData;
        }

        public UUID getWorkerUUID() {
            return workerUUID;
        }

        public Boolean getIsSent() {
            return isSent;
        }

        public void setIsSent(Boolean isSent) {
            this.isSent = isSent;
        }
    }
}
