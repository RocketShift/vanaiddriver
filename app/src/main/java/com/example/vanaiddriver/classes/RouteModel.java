package com.example.vanaiddriver.classes;

import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;

public class RouteModel implements Serializable {
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
    public Integer company_id;

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public ArrayList getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList waypoints) {
        this.waypoints = waypoints;
    }

    public ArrayList waypoints;
}
