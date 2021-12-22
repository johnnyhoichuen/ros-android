package com.johnnyhoichuen.rosandroid.mapxus;

import android.os.Handler;
import android.os.Looper;

import com.mapxus.map.mapxusmap.api.map.model.overlay.MapxusMarker;
import com.mapxus.map.mapxusmap.positioning.IndoorLocation;

import timber.log.Timber;

public class MapxusManager {

    private static final String TAG = MapxusManager.class.getSimpleName();

    private static final MapxusManager mapxusManager = new MapxusManager();

    private MapxusNavigationPositioningProvider mapxusPositioningProvider;
//    private boolean locUpdateInQueue = false;
    private Handler locationUpdateHandler;
//    private static IndoorLocation queuedLocation;
    private IndoorLocation queuedLocation;

    private MapxusManager() {
        super();
        // get main handler to access UI
        locationUpdateHandler = new Handler(Looper.getMainLooper());
    }

    public static MapxusManager getInstance() {
        return mapxusManager;
    }

    public void init(MapxusNavigationPositioningProvider provider) {
        mapxusManager.mapxusPositioningProvider = provider;
    }

    public void onStart() {
        startLocationUpdateThread();
    }

    public void onPause() {
        stopLocationUpdateThread();
    }

    public void updateQueuedLocation(IndoorLocation location) {
        getInstance().queuedLocation = location;

        // check if we're referring to the same queuedLocation
        Timber.tag(TAG).d("queuedLocation updated with id %d",
                System.identityHashCode(getInstance().queuedLocation));
    }

    public void dispatchCompassChange(float degree) {
        mapxusPositioningProvider.dispatchCompassChange(degree, com.mapxus.map.mapxusmap.positioning.SensorAccuracy.SENSOR_ACCURACY_MEDIUM);
    }

    private final Runnable locationUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            Timber.tag("locationUpdateRunnable").d("running");
            try {
//                if (getInstance().queuedLocation != null) {
                if (queuedLocation != null) {
                    mapxusPositioningProvider.dispatchIndoorLocationChange(getInstance().queuedLocation);
                    Timber.tag("locationUpdateRunnable").d("updating map with queuedLocation %d",
                        System.identityHashCode(getInstance().queuedLocation));
                } else {
                    Timber.tag("locationUpdateRunnable").d("queuedLocation is null");
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                locationUpdateHandler.postDelayed(locationUpdateRunnable, 200);
            }
        }
    };

    private void startLocationUpdateThread() {
        locationUpdateRunnable.run();
    }

    private void stopLocationUpdateThread() {
        locationUpdateHandler.removeCallbacks(locationUpdateRunnable);
    }

}
