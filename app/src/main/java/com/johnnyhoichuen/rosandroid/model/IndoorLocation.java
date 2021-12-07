package com.johnnyhoichuen.rosandroid.model;

import android.location.Location;

import androidx.annotation.Keep;

@Keep
public class IndoorLocation extends Location {

    private String buildingId;
    private String buildingName;
    private String floor;
    private float accuracy;

    public IndoorLocation() {
        super("init");
        
        // latlng == 0 by default
        
        buildingId = "";
        buildingName = "";
        floor = "";
        accuracy = 0f;
    }

    public IndoorLocation(Location location, String building, String floor) {
        super(location);

        this.buildingId = building;
        this.floor = floor;
    }

    public IndoorLocation(String provider, double latitude, double longitude, String floor,
                          String buildingId, String buildingName, float bearing, long timeStamp) {
        super(provider);

        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setTime(timeStamp);
        this.floor = floor;
        this.buildingId = buildingId;
        this.buildingName = buildingName;

        this.setBearing(bearing);
    }

    public IndoorLocation(IndoorLocation location) {
        super(location);
        this.buildingId = location.buildingId;
        this.buildingName = location.buildingName;
        this.floor = location.floor;
        this.accuracy = location.accuracy;

        this.setBearing(location.getBearing());
    }

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuildingId() {
        return this.buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(float accuracy) {
        super.setAccuracy(accuracy);
        this.accuracy = accuracy;
    }
    
    public boolean isValid() {
        if (!getProvider().equals("LIPHY") || !getProvider().equals("BLUETOOTH") || !getProvider().equals("PDR")) {
            return false;
        }

        if (getLatitude() == 0 || getLongitude() == 0) {
            return false;
        }

        return true;
    }
}
