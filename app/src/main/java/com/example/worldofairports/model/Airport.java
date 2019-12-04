package com.example.worldofairports.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Airport {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("order")
    @Expose
    private List<Double> order = null;
    @SerializedName("fields")
    @Expose
    private AirportData airportData;

    public Airport(String id, List<Double> order, AirportData airportData) {
        this.id = id;
        this.order = order;
        this.airportData = airportData;
    }

    public String getId() {
        return id;
    }

    public List<Double> getOrder() {
        return order;
    }

    public AirportData getAirportData() {
        return airportData;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOrder(List<Double> order) {
        this.order = order;
    }

    public void setAirportData(AirportData airportData) {
        this.airportData = airportData;
    }
}
