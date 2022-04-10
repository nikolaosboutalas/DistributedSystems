package worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import common.ChunkWrapper;
import common.Result;
import common.ResultWrapper;
import common.gpx.model.Waypoint;

public class Worker {
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String hostName = getHostName(scanner);
        Integer portNumber = getPortNumber(scanner);
        System.out.println("[WORKER] Connecting to " + hostName + ":" + portNumber);
        try (Socket socket = new Socket(hostName, portNumber)) {
            System.out.println("[WORKER] Connected to " + socket);
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            while (true) {
                ChunkWrapper chunkWrapper = (ChunkWrapper) inputStream.readObject();
                System.out.println("[WORKER] Received chunk " + chunkWrapper.getChunkId());
                new Thread(new WorkerThread(outputStream, chunkWrapper)).start();
            }
        }
    }

    private static String getHostName(Scanner scanner) {
        System.out.print("[WORKER] Please choose a host name: ");
        return scanner.nextLine();
    }

    private static Integer getPortNumber(Scanner scanner) {
        System.out.print("[WORKER] Please choose a port number: ");
        return Integer.parseInt(scanner.nextLine());
    }

    private static class WorkerThread implements Runnable {
        private ObjectOutputStream objectOutputStream;
        private ChunkWrapper chunkWrapper;

        public WorkerThread(ObjectOutputStream outputStream, ChunkWrapper chunkWrapper) {
            this.objectOutputStream = outputStream;
            this.chunkWrapper = chunkWrapper;
        }

        @Override
        public void run() {
            try {
                Result result = map(chunkWrapper.getChunk());
                synchronized (this.objectOutputStream) {
                    this.objectOutputStream.writeObject(new ResultWrapper(chunkWrapper.getChunkId(), result));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Result map(ArrayList<Waypoint> waypoints) {

            double totalDistance = 0.0;
            double totalElevation = 0.0;
            double totalTime = 0;

            for (int i = 0; i < waypoints.size() - 1; i++) {
                Waypoint currentWaypoint = waypoints.get(i);
                Waypoint nextWaypoint = waypoints.get(i + 1);
                double distance = calculateDistance(currentWaypoint, nextWaypoint);
                totalDistance += distance;
                if (Double.parseDouble(nextWaypoint.getEle()) > Double.parseDouble(currentWaypoint.getEle()))
                    totalElevation += Double.parseDouble(nextWaypoint.getEle())
                            - Double.parseDouble(currentWaypoint.getEle());
                totalTime += calculateTimestampDifference(currentWaypoint.getTime(), nextWaypoint.getTime());
            }

            double averageVelocity = totalDistance / totalTime;

            return new Result(
                    totalDistance,
                    totalElevation,
                    averageVelocity,
                    totalTime);
        }

        private double calculateDistance(Waypoint waypoint1, Waypoint waypoint2) {

            // distance using the Haversine formula

            double earthRadius = 6371000; // radius of the earth in meters
            double lat1 = Math.toRadians(Double.parseDouble(waypoint1.getLat()));
            double lon1 = Math.toRadians(Double.parseDouble(waypoint1.getLon()));
            double lat2 = Math.toRadians(Double.parseDouble(waypoint2.getLat()));
            double lon2 = Math.toRadians(Double.parseDouble(waypoint2.getLon()));
            double deltaLat = lat2 - lat1;
            double deltaLon = lon2 - lon1;
            double chordLengthSquared = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                    Math.cos(lat1) * Math.cos(lat2) *
                            Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
            double angularDistance = 2 * Math.atan2(Math.sqrt(chordLengthSquared), Math.sqrt(1 - chordLengthSquared));
            return earthRadius * angularDistance;
        }

        private static double calculateTimestampDifference(String timestamp1, String timestamp2) {
            LocalDateTime dateTime1 = LocalDateTime.parse(timestamp1, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime dateTime2 = LocalDateTime.parse(timestamp2, DateTimeFormatter.ISO_DATE_TIME);

            Duration duration = Duration.between(dateTime1, dateTime2);
            return duration.getSeconds();
        }
    }
}
