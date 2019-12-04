package com.example.worldofairports.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AirportData {

    @SerializedName("lat")
    @Expose
    private Double latitude;
    @SerializedName("lon")
    @Expose
    private Double longitude;
    @SerializedName("name")
    @Expose
    private String name;

    public AirportData(Double latitude, Double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }
}
