package common.gpx.model;
import java.util.ArrayList;

public class Segment {
    private String name;
    private ArrayList<Waypoint> waypoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    @Override
    public String toString() {
        return "Segment [name=" + name + ", waypoints=" + waypoints + "]";
    }
}