package com.johnnyhoichuen.rosandroid.mapxus;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.mapxus.map.mapxusmap.positioning.IndoorLocation;
import com.mapxus.map.mapxusmap.positioning.IndoorLocationProvider;
import com.mapxus.positioning.positioning.api.ErrorInfo;
import com.mapxus.positioning.positioning.api.MapxusLocation;
import com.mapxus.positioning.positioning.api.MapxusPositioningClient;
import com.mapxus.positioning.positioning.api.MapxusPositioningListener;
import com.mapxus.positioning.positioning.api.PositioningState;

public class MapxusNavigationPositioningProvider extends IndoorLocationProvider {

    private static final String TAG = "PositioningProvider";

//    private WeakReference<Context> context;
    private Context context;
    LifecycleOwner lifecycleOwner;
    private MapxusPositioningClient positioningClient;
    private boolean started;
    public float currentOrientation = 700f;

    public enum IndoorPositioningProvider {
        LIPHY,
        MAPXUS
    }

    public IndoorPositioningProvider provider = IndoorPositioningProvider.LIPHY;

    public MapxusNavigationPositioningProvider(LifecycleOwner lifecycleOwner, Context context) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
//        this.context = new WeakReference<Context>(context);

//        if (BuildConfig.DEBUG) {
//            Timber.plant(new Timber.DebugTree());
//        }

    }

//    public MapxusNavigationPositioningProvider(Context context) {
//        this.context = new WeakReference<Context>(context);
//    }

    @Override
    public boolean supportsFloor() {
        return true;
    }

    @Override
    public void start() {
        // before mapxus 4.0.0
//        positioningClient = MapxusPositioningClient.getInstance(context.get().getApplicationContext());
//        positioningClient.setPositioningListener(mapxusPositioningListener);
//        positioningClient.start();
//        started = true;

        if (context != null) {
            // mapxus 4.0.0
            positioningClient = MapxusPositioningClient.getInstance(lifecycleOwner, context);
            positioningClient.addPositioningListener(mapxusPositioningListener);
            positioningClient.start();
            started = true;
        }

    }

    @Override
    public void stop() {
        if (positioningClient != null) {
            positioningClient.stop();
        }
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }


    private MapxusPositioningListener mapxusPositioningListener = new MapxusPositioningListener() {
        @Override
        public void onStateChange(PositioningState positionerState) {
            switch (positionerState) {
                case STOPPED: {
                    dispatchOnProviderStopped();
                    break;
                }
                case RUNNING: {
                    dispatchOnProviderStarted();
                    break;
                }
                default:
                    break;
            }
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
            Log.e(TAG, errorInfo.getErrorMessage());
            dispatchOnProviderError(new com.mapxus.map.mapxusmap.positioning.ErrorInfo(errorInfo.getErrorCode(), errorInfo.getErrorMessage()));
        }

        @Override
        public void onOrientationChange(float orientation, int sensorAccuracy) {
            // use the compass from LiphyLocationService
//            if(Math.abs(orientation-currentOrientation)>5) {
//                dispatchCompassChange(orientation, sensorAccuracy);
//                currentOrientation = orientation;
//            }
        }

        @Override
        public void onLocationChange(MapxusLocation positioningLocation) {
            if (positioningLocation == null) {
                return;
            }
            Location location = new Location("MapxusPositioning");
            location.setLatitude(positioningLocation.getLatitude());
            location.setLongitude(positioningLocation.getLongitude());
            location.setTime(System.currentTimeMillis());
            String floor = positioningLocation.getMapxusFloor() == null ? null : positioningLocation.getMapxusFloor().getCode();
            String building = positioningLocation.getBuildingId();
            IndoorLocation indoorLocation = new IndoorLocation(location, building, floor);
            indoorLocation.setAccuracy((float) positioningLocation.getAccuracy());

            Log.d(TAG,"POSTIONPROVIDER:Accuracy"+String.valueOf(indoorLocation.getAccuracy()));

            if (provider == IndoorPositioningProvider.LIPHY) {
                //屏蔽定位返回 i.e. no GPS now
                // do nth
//                Timber.d("using Liphy as location provider");
            }
//            else if (provider == IndoorPositioningProvider.MAPXUS) {
//                IndoorLocation liphyLocation = new IndoorLocation("MAPXUS",
//                        location.getLatitude(), location.getLongitude(), indoorLocation.getFloor(),
//                        indoorLocation.getBuilding(), "", indoorLocation.getBearing(), System.currentTimeMillis());
////                LiphyState.setCurrentLocation(liphyLocation);
//                dispatchIndoorLocationChange(indoorLocation);
//            }
        }
    };
}
