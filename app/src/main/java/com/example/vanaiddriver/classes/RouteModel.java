package com.example.vanaiddriver.classes;

import java.sql.Array;

public class RouteModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrigin_lat() {
        return origin_lat;
    }

    public void setOrigin_lat(String origin_lat) {
        this.origin_lat = origin_lat;
    }

    public String getOrigin_lng() {
        return origin_lng;
    }

    public void setOrigin_lng(String origin_lng) {
        this.origin_lng = origin_lng;
    }

    public String getDestination_lat() {
        return destination_lat;
    }

    public void setDestination_lat(String destination_lat) {
        this.destination_lat = destination_lat;
    }

    public String getDetination_lng() {
        return detination_lng;
    }

    public void setDetination_lng(String detination_lng) {
        this.detination_lng = detination_lng;
    }

    public String name;
    public String description;
    public String origin_lat;
    public String origin_lng;
    public String destination_lat;
    public String detination_lng;
}
