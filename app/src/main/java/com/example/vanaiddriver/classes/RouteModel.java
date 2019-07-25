package com.example.vanaiddriver.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class RouteModel implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private String origin_lat;
    private String origin_lng;
    private String destination_lat;
    private String destination_lng;
    private Integer company_id;
    private ArrayList waypoints;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompany_id() {
        return company_id;
    }

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

    public String getDestination_lng() {
        return destination_lng;
    }

    public void setDestination_lng(String detination_lng) {
        this.destination_lng = detination_lng;
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
}
