package common.gpx;

import java.util.ArrayList;

import common.gpx.model.GPX;
import common.gpx.model.Waypoint;

public class GPXParser {
    static public GPX parse(String gpxString) {
        GPX gpxData = new GPX();
        System.out.println("Parsing" + gpxString);
        gpxData.setCreator(gpxString.split("creator=\"")[1].split("\"")[0]);
        gpxData.setWaypoints(new ArrayList<>());
        String[] waypoints = gpxString.split("<wpt");
        for (int i = 1; i < waypoints.length; i++) {
            String waypoint = waypoints[i];
            Waypoint w = new Waypoint();
            w.setLat(waypoint.split("lat=\"")[1].split("\"")[0]);
            w.setLon(waypoint.split("lon=\"")[1].split("\"")[0]);
            w.setEle(waypoint.split("<ele>")[1].split("</ele>")[0]);
            w.setTime(waypoint.split("<time>")[1].split("</time>")[0]);
            gpxData.getWaypoints().add(w);
        }
        return gpxData;
    }
}
