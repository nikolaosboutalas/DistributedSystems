package common;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import common.Result.Stat;

public class Statistics {
    private HashMap<String, Integer> runCount;
    private HashMap<String, Double> avgDistance;
    private HashMap<String, Double> avgElevation;
    private HashMap<String, Double> avgElapsedTimeInSeconds;
    private Double totalAvgDistance = 0.0;
    private Double totalAvgElevation = 0.0;
    private Double totalAvgElapsedTimeInSeconds = 0.0;

    public Statistics() {
        this.runCount = new HashMap<>();
        this.avgDistance = new HashMap<>();
        this.avgElevation = new HashMap<>();
        this.avgElapsedTimeInSeconds = new HashMap<>();
    }

    public synchronized void calculateNewAvgFor(String user, Result result) {
        if (runCount.get(user) == null) {
            runCount.put(user, 0);
            avgDistance.put(user, 0.0);
            avgElevation.put(user, 0.0);
            avgElapsedTimeInSeconds.put(user, 0.0);
        }
        Integer oldRunCount = runCount.get(user);
        Double oldAvgDistance = avgDistance.get(user);
        Double oldAvgElevation = avgElevation.get(user);
        Double oldAvgElapsedTimeInSeconds = avgElapsedTimeInSeconds.get(user);
        Integer newRunCount = oldRunCount + 1;
        Double newAvgDistance = (oldAvgDistance * oldRunCount + result.getDistance()) / newRunCount;
        Double newAvgElevation = (oldAvgElevation * oldRunCount + result.getElevation()) / newRunCount;
        Double newAvgElapsedTimeInSeconds = (oldAvgElapsedTimeInSeconds * oldRunCount + result.getElapsedTimeInSeconds()) / newRunCount;
        runCount.put(user, newRunCount);
        avgDistance.put(user, newAvgDistance);
        avgElevation.put(user, newAvgElevation);
        avgElapsedTimeInSeconds.put(user, newAvgElapsedTimeInSeconds);
        Integer numberOfDistinctUsers = runCount.keySet().size();
        totalAvgDistance = (totalAvgDistance * (numberOfDistinctUsers - 1) + newAvgDistance) / numberOfDistinctUsers;
        totalAvgElevation = (totalAvgElevation * (numberOfDistinctUsers - 1) + newAvgElevation) / numberOfDistinctUsers;
        totalAvgElapsedTimeInSeconds = (totalAvgElapsedTimeInSeconds * (numberOfDistinctUsers - 1) + newAvgElapsedTimeInSeconds) / numberOfDistinctUsers;
    }

    public synchronized String getStatsFor(String user) {
        return "Run count: " + runCount.get(user) + "\n" +
               "Average distance: " + avgDistance.get(user) + "\n" +
               "Average elevation: " + avgElevation.get(user) + "\n" +
               "Average elapsed time: " + avgElapsedTimeInSeconds.get(user) + "\n";
    }

    public synchronized String getTotalStatsString() {
        StringBuilder leaderboard = new StringBuilder();
        leaderboard.append("Total average distance: ").append(totalAvgDistance).append("\n");
        leaderboard.append("Total average elevation: ").append(totalAvgElevation).append("\n");
        leaderboard.append("Total average elapsed time: ").append(totalAvgElapsedTimeInSeconds).append("\n");
        leaderboard.append("Leaderboard based on performance (Average Velocity-Speed):").append("\n");

        Map<String, Double> avgVelocities = new HashMap<>();
        for (String user : avgDistance.keySet()) {
            Double avgDistance = this.avgDistance.get(user);
            Double avgTime = this.avgElapsedTimeInSeconds.get(user);
            Double avgVelocity = avgDistance / avgTime;
            avgVelocities.put(user, avgVelocity);
        }

        Map<String, Double> sortedUsers = avgVelocities.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        int rank = 1;
        for (Map.Entry<String, Double> entry : sortedUsers.entrySet()) {
            leaderboard.append("Rank ").append(rank).append(": ")
                    .append("User: ").append(entry.getKey()).append(", ")
                    .append("Average Velocity: ").append(entry.getValue()).append("\n");
            rank++;
        }

        return leaderboard.toString();
    }

    public synchronized Stat getTotalStats() {

        Stat stat = new Stat();
        stat.averageTotalDistance = totalAvgDistance;
        stat.averageTotalElevation = totalAvgElevation;
        stat.averageTotalElapsedTime = totalAvgElapsedTimeInSeconds;

        Map<String, Double> avgVelocities = new HashMap<>();
        for (String user : avgDistance.keySet()) {
            Double avgDistance = this.avgDistance.get(user);
            stat.averageUserDistance = avgDistance;
            Double avgTime = this.avgElapsedTimeInSeconds.get(user);
            stat.averageUserElapsedTime = avgTime;
            Double avgElevation = this.avgElevation.get(user);
            stat.averageUserElevation = avgElevation;
            Double avgVelocity = avgDistance / avgTime;
            avgVelocities.put(user, avgVelocity);
        }

        Map<String, Double> sortedUsers = avgVelocities.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        
        stat.leaderboard = sortedUsers;
        return stat;
    }
}
