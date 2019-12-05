package com.example.worldofairports.model;

import java.util.Comparator;

public class AirportAndDistance {

    private Airport airport;
    private Double distanceToUserCoordinates;

    public AirportAndDistance(Airport airport, Double distanceToUserCoordinates) {
        this.airport = airport;
        this.distanceToUserCoordinates = distanceToUserCoordinates;
    }

    public Airport getAirport() {
        return airport;
    }

    public Double getDistanceToUserCoordinates() {
        return distanceToUserCoordinates;
    }

    //custom comparator for this class
    public static class SortByDistance implements Comparator<AirportAndDistance> {

        @Override
        public int compare(AirportAndDistance a, AirportAndDistance b) {
            return a.getDistanceToUserCoordinates().compareTo(b.getDistanceToUserCoordinates());
        }
    }
}
