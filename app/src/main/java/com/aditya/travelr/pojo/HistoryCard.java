package com.aditya.travelr.pojo;

/**
 * Created by devad_000 on 30-06-2015.
 */
public class HistoryCard {
    private String nickname;
    private String description;
    private String reverse;
    private double lat;
    private double lon;
    private long timeStamp;
    private int _id;

    public HistoryCard(int _id, String nickname, String reverse, double lat, double lon, String desc, long timeStamp){
        this._id = _id;
        this.nickname = nickname;
        this.reverse = reverse;
        this.lat = lat;
        this.lon = lon;
        this.description = desc;
        this.timeStamp = timeStamp;
    }

    public void setReverse(String reverse) {
        this.reverse = reverse;
    }

    public String getReverse() {
        return reverse;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int get_id() {
        return _id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDescription() {
        return description;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
