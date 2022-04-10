package master.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import common.Result;
import common.Statistics;
import common.gpx.GPXParser;
import common.gpx.model.GPX;
import common.gpx.model.Waypoint;
import common.mapreduce.DataStore;
import common.gpx.model.Segment;

public class ClientHandler extends Handler {

    private Statistics statistics;

    public ClientHandler(Socket clientSocket, DataStore<ArrayList<Waypoint>, Result> dataStore, Statistics statistics) {
        super(clientSocket, dataStore);
        this.statistics = statistics;
    }

    @Override
    protected void handle() throws IOException, InterruptedException {
        String fileContent = readFileFromSocket();
        GPX gpxData = GPXParser.parse(fileContent);
        ArrayList<Integer> chunkIds = new ArrayList<>();
        chunk(gpxData).forEach(chunk -> {
            chunkIds.add(this.dataStore.add(chunk));
        });
        ArrayList<Result> intermediateResults = new ArrayList<>();
        for (Integer chunkId : chunkIds) {
            intermediateResults.add(this.dataStore.getProcessedData(chunkId));
        }
        Result finalResult = reduce(intermediateResults);
        this.statistics.calculateNewAvgFor(gpxData.getCreator(), finalResult);
        System.out.println(this.statistics.getStatsFor(gpxData.getCreator()));
        System.out.println(this.statistics.getTotalStatsString());
        finalResult.setStat(this.statistics.getTotalStats());
        ObjectOutputStream outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        outputStream.writeObject(finalResult);
    }

    private Result reduce(ArrayList<Result> intermediateResults) {
        Result finalResult = new Result();
        Double speed = 0.0;
        for (Result result : intermediateResults) {
            finalResult.setDistance(finalResult.getDistance() + result.getDistance());
            finalResult
                    .setElapsedTimeInSeconds(finalResult.getElapsedTimeInSeconds() + result.getElapsedTimeInSeconds());
            finalResult.setElevation(finalResult.getElevation() + result.getElevation());
            speed += result.getSpeed();
        }
        finalResult.setSpeed(speed / intermediateResults.size());
        return finalResult;
    }

    private String readFileFromSocket() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        StringBuilder fileContentBuilder = new StringBuilder();
        InputStream inputStream = this.clientSocket.getInputStream();

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            String newContent = new String(buffer, 0, bytesRead);
            System.out.println("Received new file content: " + newContent);
            fileContentBuilder.append(newContent);
            if (newContent.contains("</gpx>")) break;
        }

        String fileContent = fileContentBuilder.toString();
        System.out.println("Received file content: " + fileContent);
        return fileContent;
    }

    private static ArrayList<ArrayList<Waypoint>> chunk(GPX gpxData) {
        int chunkSize = 10;
        int numberOfWaypoints = gpxData.getWaypoints().size();
        int numberOfChunks = numberOfWaypoints / chunkSize;
        ArrayList<ArrayList<Waypoint>> chunks = new ArrayList<>();
        if (numberOfWaypoints % chunkSize != 0)
            numberOfChunks++;
        for (int i = 0; i < numberOfChunks; i++) {
            int start = i * chunkSize;
            int end = (i + 1) * chunkSize;
            if (end > numberOfWaypoints)
                end = numberOfWaypoints;
            ArrayList<Waypoint> chunk = new ArrayList<Waypoint>(gpxData.getWaypoints().subList(start, end));
            chunks.add(chunk);
        }
        return chunks;
    }

    @Override
    protected String type() {
        return "CLIENT";
    }

}
