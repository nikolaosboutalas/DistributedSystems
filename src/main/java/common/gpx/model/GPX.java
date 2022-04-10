package common.gpx.model;

import java.util.ArrayList;


public class GPX {
    private String creator;
    private ArrayList<Waypoint> waypoints;
    private ArrayList<Segment> segments = new ArrayList<>();

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public ArrayList<Segment> getSegments() {
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    @Override
    public String toString() {
        return "GPX [creator=" + creator + ", waypoints=" + waypoints + ", segments=" + segments + "]";
    }
}