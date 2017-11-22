package com.trashdudes.mapalarm;

import java.io.Serializable;

/**
 * Created by guilhermen on 10/25/17.
 */

public class AlarmModel implements Serializable {

    private Integer id;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private String notes;

    public AlarmModel() {

    }

    public AlarmModel(Integer id, Double latitude, Double longitude, Double radius, String notes) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getRadius() {
        return radius;
    }

    public String getNotes() {
        return notes;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
