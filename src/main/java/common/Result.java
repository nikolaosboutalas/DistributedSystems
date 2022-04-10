package common;

import java.io.Serializable;
import java.util.Map;

public class Result implements Serializable {
    private Double distance;
    private Double elevation;
    private Double speed;
    private Double elapsedTimeInSeconds;
    private Stat stat = null;

    public Result(Double distance, Double elevation, Double speed, Double elapsedTime) {
        this.distance = distance;
        this.elevation = elevation;
        this.speed = speed;
        this.elapsedTimeInSeconds = elapsedTime;
    }

    public Result() {
        this.distance = 0.0;
        this.elevation = 0.0;
        this.speed = 0.0;
        this.elapsedTimeInSeconds = 0.0;
    }

    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    public Double getElevation() {
        return elevation;
    }
    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }
    public Double getSpeed() {
        return speed;
    }
    public void setSpeed(Double speed) {
        this.speed = speed;
    }
    public Double getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }
    public void setElapsedTimeInSeconds(Double elapsedTime) {
        this.elapsedTimeInSeconds = elapsedTime;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    @Override
    public String toString() {
        return "Result [distance=" + distance + ", elevation=" + elevation + ", speed=" + speed + ", elapsedTime="
                + elapsedTimeInSeconds + ", stat=" + stat + "]";
    }


    static public class Stat implements Serializable {
        public Double averageUserDistance;
        public Double averageUserElevation;
        public Double averageUserElapsedTime;
        public Double averageTotalDistance;
        public Double averageTotalElevation;
        public Double averageTotalElapsedTime;
        public Map<String, Double> leaderboard;

        @Override
        public String toString() {
            return "Stat [averageUserDistance=" + averageUserDistance + ", averageUserElevation=" + averageUserElevation
                    + ", averageUserElapsedTime=" + averageUserElapsedTime + ", averageTotalDistance="
                    + averageTotalDistance + ", averageTotalElevation=" + averageTotalElevation
                    + ", averageTotalElapsedTime=" + averageTotalElapsedTime + ", leaderboard=" + leaderboard + "]";
        }
    }
}

