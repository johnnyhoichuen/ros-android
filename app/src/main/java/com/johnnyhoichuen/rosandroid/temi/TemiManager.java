package com.johnnyhoichuen.rosandroid.temi;

import android.app.Application;

import com.mapxus.map.mapxusmap.api.map.model.LatLng;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.navigation.listener.OnCurrentPositionChangedListener;
import com.robotemi.sdk.navigation.model.Position;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import timber.log.Timber;

public class TemiManager implements OnRobotReadyListener, OnLocationsUpdatedListener,
        OnCurrentPositionChangedListener, OnGoToLocationStatusChangedListener {

    private static final TemiManager instance = new TemiManager();

    // TODO: 13 Dec 2021 update according to venue
    public static final double homeBaseLat = 22.334699;
    public static final double homeBaseLng = 114.263493;
    private static double latScalingFactor = 0.000009621;
    private static double lngScalingFactor = 0.000010382;

    // TODO: 13 Dec 2021 adaptive to different venue
    //  check current building & use cloud manager to get building info
    //  and simplify this by calculating lat/lngComponent once only (per venue)
//    public static double latComponent;
//    public static double lngComponent;

    private Position lastPosition;

    private boolean isReady;
    private MutableLiveData<Position> currentPosition;

    public static TemiManager getInstance() {
        return instance;
    }

    public TemiManager() {
        isReady = false;
    }

    @Override
    public void onRobotReady(boolean isReady) {
        Timber.tag("temi").d("onRobotReady: %b", isReady);
        this.isReady = isReady;
    }

    @Override
    public void onGoToLocationStatusChanged(@NotNull String location, @NotNull String status, int descriptionId, @NotNull String description) {
//        Timber.tag("temi").d("onGoToLocationStatusChanged: location = %s, status = %s, descriptionId = %i, description = %s",
//            location, status, descriptionId, description);
//        Timber.tag("temi").d("onGoToLocationStatusChanged: " + location + " " + status + " " +
//            descriptionId + " " + description);


    }
    @Override
    public void onLocationsUpdated(@NotNull List<String> locations) {
        for (String location : locations) {
            Timber.tag("temi").d("onLocationsUpdated: location = %s", location);
        }
    }

    @Override
    public void onCurrentPositionChanged(@NotNull Position position) {
        Timber.tag("temi").d("onCurrentPositionChanged: position = %s", position);

        if (currentPosition == null) {
            currentPosition = new MutableLiveData<>();
            currentPosition.setValue(position);
        }

        currentPosition.setValue(position);
    }

    public void init() {
        Robot.getInstance().addOnRobotReadyListener(this);
        Robot.getInstance().addOnGoToLocationStatusChangedListener(this);
        Robot.getInstance().addOnLocationsUpdatedListener(this);
        Robot.getInstance().addOnCurrentPositionChangedListener(this);
    }

    public void deinit() {
        Robot.getInstance().removeOnRobotReadyListener(this);
        Robot.getInstance().removeOnGoToLocationStatusChangedListener(this);
        Robot.getInstance().removeOnLocationsUpdateListener(this);
        Robot.getInstance().removeOnCurrentPositionChangedListener(this);
    }

    public void goToPosition(Position position) {
        Robot.getInstance().goToPosition(position);
    }

    public LatLng translateToLatlng(double x, double y, double angle) {

//              // TODO: 10 Dec 2021 remove this simple method
//                // simple way to calculate
//                final double latScaleFactor = 0.000009621;
//                final double lngScaleFactor = 0.000010382;
//                double simpleLatComponent = -position.getX();
//                double simpleLngComponent = position.getY();
//
//                Timber.tag("temi").d("position simple latComp, lngComp: (%f, %f)",
//                        simpleLatComponent, simpleLngComponent);
//
//                double simpleLatitude = ((simpleLatComponent * latScaleFactor) + TemiManager.homeBaseLat);
//                double simpleLongitude = ((simpleLngComponent * lngScaleFactor) + TemiManager.homeBaseLng);
//
//                Timber.tag("temi").d("position simple latlng: (%f, %f), yaw in degree: %f",
//                        simpleLatitude, simpleLongitude, degree);

        double sinAngle = 0, cosAngle = 0;

        // solving -0 issue
        if (angle == 90 || angle == 270) {
            cosAngle = 0;

            if (angle == 90) sinAngle = 1;
            else if (angle == 270) sinAngle = -1;
        } else if (angle == 0 || angle == 180 || angle == 360) {
            sinAngle = 0;

            if (angle == 0 || angle == 360) {
                cosAngle = 1;
            } else if (angle == 180) {
                cosAngle = -1;
            }
        } else {
            double angleInRadian = Math.toRadians(angle);
            sinAngle = Math.sin(angleInRadian);
            cosAngle = Math.cos(angleInRadian);
        }

        double xsin = x * sinAngle;
        double ysin = y * sinAngle;
        double xcos = x * cosAngle;
        double ycos = y * cosAngle;

        // bug: negative positive * 0 = negative, like wtf
        if (cosAngle == 0) {
            if (x < 0) {
                xcos = -xcos;
            } else if (y < 0) {
                ycos = -ycos;
            }
        }

        double latComponent = ycos - xsin;
        double lngComponent = xcos + ysin;

//        Timber.tag("temi").d("position times sin * x, sin * y: (%f, %f)", x * sinAngle, y * sinAngle);
//        Timber.tag("temi").d("position times cos * x, cos * y: (%f, %f)", x * cosAngle, y * cosAngle);
//        Timber.tag("temi").d("position [latComp, lngComp: (%f, %f)], [sinAngle, cosAngle: (%f, %f)]",
//                latComponent, lngComponent, sinAngle, cosAngle);

        double latitude = ((latComponent * latScalingFactor) + TemiManager.homeBaseLat);
        double longitude = ((lngComponent * lngScalingFactor) + TemiManager.homeBaseLng);

        return new LatLng(latitude, longitude);
    }

    public Position translateToTemiCoor(double latitude, double longitude, double angle) {
        Timber.tag("temi").d("translateToTemiCoor");

        double latComponent = (latitude - homeBaseLat) / latScalingFactor;
        double lngComponent = (longitude - homeBaseLng) / lngScalingFactor;
        
        // rotate the component back to x,y's orientation
        double sinAngle, cosAngle;

        // solving -0 issue
        if (angle == 90 || angle == 270) {
            cosAngle = 0; // ensure it's 0
            if (angle == 90) sinAngle = 1;
            else sinAngle = -1;
        } else if (angle == 0 || angle == 180 || angle == 360) {
            sinAngle = 0; // ensure it's 0
            if (angle == 0 || angle == 360) cosAngle = 1;
            else cosAngle = -1;
        } else {
            double angleInRadian = Math.toRadians(angle);
            sinAngle = Math.sin(angleInRadian);
            cosAngle = Math.cos(angleInRadian);
        }

        double xsin = latComponent * sinAngle;
        double ysin = lngComponent * sinAngle;
        double xcos = latComponent * cosAngle;
        double ycos = lngComponent * cosAngle;

        // bug: negative positive * 0 = negative, like wtf
        if (cosAngle == 0) {
            if (latComponent < 0) xcos = -xcos;
            else if (lngComponent < 0) ycos = -ycos;
        }

        // apply rotation matrix
        double x = -xsin + ycos;
        double y = xcos + ysin;

        Position position = new Position();
        position.setX((float) x);
        position.setY((float) y);

        Timber.tag("temi").d("latlng to xy");
        Timber.tag("temi").d("x = %f", position.getX());
        Timber.tag("temi").d("y = %f", position.getY());

        return position;
    }

    public LiveData<Position> getPosition() {
        // this causes model.getPosition().observe(...) to crash
//        if (!getIsRobotReady()) return null;

        if (currentPosition == null) {
            currentPosition = new MutableLiveData<>();
        }

        return currentPosition;
    }

    /**
     * The original yaw from temi ranges from ~180 to -180
     * @param yaw
     * @return
     */
    public double getYawInDegree(double yaw) {
        double degree = Math.toDegrees(yaw);

        // reverse the rotation
        degree += 180;
        if (degree >= 360)
            degree = degree % 360;

//        degree > 180 ? degree = 360 - degree :
        degree = 360 - degree;

        return degree;
    }

    public boolean isRobotReady() {
        return isReady;
    }

    public void setLastPosition(Position position) {
        this.lastPosition = position;
    }

    public Position getLastPosition() {
        return this.lastPosition;
    }
}
