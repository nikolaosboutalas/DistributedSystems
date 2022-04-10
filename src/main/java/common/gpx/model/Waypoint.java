package common.gpx.model;

import java.io.Serializable;

public class Waypoint implements Serializable {
    private String lat;
    private String lon;
    private String ele;
    private String time;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getEle() {
        return ele;
    }

    public void setEle(String ele) {
        this.ele = ele;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Waypoint [lat=" + lat + ", lon=" + lon + ", ele=" + ele + ", time=" + time + "]";
    }
}