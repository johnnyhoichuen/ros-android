package com.johnnyhoichuen.rosandroid.ui.fragments.map;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import timber.log.Timber;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapxus.map.mapxusmap.api.map.MapViewProvider;
import com.mapxus.map.mapxusmap.api.map.MapxusMap;
import com.mapxus.map.mapxusmap.api.map.interfaces.OnMapxusMapReadyCallback;
import com.mapxus.map.mapxusmap.api.services.RoutePlanning;
import com.mapxus.map.mapxusmap.impl.MapboxMapViewProvider;
import com.mapxus.map.mapxusmap.positioning.IndoorLocation;
import com.mapxus.map.mapxusmap.positioning.IndoorLocationProviderListener;
import com.johnnyhoichuen.rosandroid.R;
import com.johnnyhoichuen.rosandroid.mapxus.MapxusNavigationPositioningProvider;
import com.johnnyhoichuen.rosandroid.viewmodel.MapxusViewModel;
import org.jetbrains.annotations.NotNull;

public class MapxusFragment extends Fragment implements OnMapReadyCallback, OnMapxusMapReadyCallback,
    // DataListener, WidgetChangeListener, // ROS Mobile
    MapxusViewModel.OnTemiLocationChangedListener, MapxusViewModel.OnRobotLocationChangedListener
{

    private static final String TAG = MapxusFragment.class.getSimpleName();

    private MapxusViewModel mViewModel;

    private RoutePlanning routePlanning = RoutePlanning.newInstance();
    private MapView mapView;
    private MapViewProvider mapViewProvider;
    private MapboxMap mapboxMap;
    private MapxusMap mapxusMap;

    // for locating the bluedot
    private MapxusNavigationPositioningProvider mapxusPositioningProvider;
//    private boolean locUpdateInQueue = false;
//    private Handler locationUpdateHandler;
//    private com.mapxus.map.mapxusmap.positioning.IndoorLocation queuedLocation;

    //    private MapxusPoseReceiver mapxusPoseViewGroup;
    private SymbolManager symbolManager;
    private OnSymbolClickListener onMapboxMarkerClickListener;
    private LocationComponentOptions mapBoxDefaultLocationComponentOptions;
    private LocationComponentActivationOptions mapBoxCustomLocationComponentActivationOptions;
    private Object mapBoxDefaultLocationComponentActivationOptions;
    private LocationComponentOptions mapBoxCustomLocationOptions;
//    private double[] location_coors = new double[2];
//    private PoseSubscriber poseSubscriber;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mapxus_map, container, false);

        // mapxus
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapViewProvider = new MapboxMapViewProvider(requireActivity(), mapView);
        mapViewProvider.getMapxusMapAsync(this);

        // view model
        mViewModel = new ViewModelProvider(this).get(MapxusViewModel.class);

        // register listener for temi & robot location
        mViewModel.registerOnTemiLocationChangedListener(this);
        mViewModel.registerOnRobotLocationChangedListener(this);

        // ignore this for now
//        mViewModel.getRosData().observe(getViewLifecycleOwner(), data -> {
//            if (data.getMessage() instanceof Odometry) {
//                Odometry pose = (Odometry) data.getMessage();
//
//                if (data.getTopic().name.equals("/odom")) {
//                    double x = pose.getPose().getPose().getPosition().getX();
//                    double y = pose.getPose().getPose().getPosition().getY();
//
//                    Quaternion q = pose.getPose().getPose().getOrientation();
//
//////                    ArrayList<Double> originalQ = new ArrayList<>();
////                    double[] originalQ = {q.getW(), q.getX(), q.getY(), q.getZ()};
////                    double[] norm = new double[4];
////
////                    for (int i = 0; i < 4; i++){
////                        norm[i] = Math.sqrt(Math.pow(originalQ[i], 2) + 1);
////                    }
////
////                    double[] newQ = new double[4];
////                    for (int i = 0; i < 4; i++) {
////                        newQ[i] = originalQ[i] / norm[i];
////
////                        if (newQ[i] < 0) newQ[i] = -newQ[i];
////                    }
////
////                    double[] eul = new double[3];
////                    eul[0] = Math.atan2(2*())
//
//                    Log.d(TAG, "odom q (w, x, y, z): " + q.getW() + ", " + q.getX() + ", " + q.getY() + ", "+ q.getZ() + ", ");
//
////                    double orientation = Math.atan2(2.0*(q.getY()*q.getZ() + q.getW()*q.getX()),
////                        q.getW()*q.getW() - q.getX()*q.getX() - q.getY()*q.getY() + q.getZ()*q.getZ());
//                    double orientation = Math.atan2(2.0*(q.getZ()*q.getW() + q.getX()*q.getY()),
//                        - 1 + (2.0 * (q.getW()*q.getW() + q.getX()*q.getX())));
////                    double orientation = (Math.asin(q.getZ()) * 2) * 180 / Math.PI; // 90 degrees shift
//
////atan2(2.0 * (q.q3 * q.q0 + q.q1 * q.q2) , - 1.0 + 2.0 * (q.q0 * q.q0 + q.q1 * q.q1));
//
//                    // change to radian
//                    orientation = Math.toDegrees(orientation);
//
//                    // reverse the rotation
//                    orientation += 180;
//                    if (orientation >= 360)
//                        orientation = orientation % 360;
//
//                    // and 90 degree shift
//                    orientation = 360 - orientation - 90;
//
//
//
//
//
//                    Log.d(TAG, "odom topic: " + data.getTopic().name + data.getTopic().type);
//                    Log.d(TAG, "odom data (x, y, q, orientation): " + x + ", " + y + ", " + orientation);
////                    Log.d(TAG, "odom data q (x, y, q, orientation): " + x + ", " + y + ", " + orientation);
//
//                    // display the icon on map if the MAP IS READY
//                    if (mapboxMap != null && mapxusMap != null) {
//                        // CYT building corner as origin
//                        double originLat = 22.334566;
//                        double originLng = 114.263432;
//
//                        // simple way to calculate
//                        final float latScaleFactor = (float) 0.00001;
//                        final float lngScaleFactor = (float) 0.00001045;
//
//                        // TODO: simplify this by calculating rotatedX & rotatedY once only (per venue)
////                        int angleDiff = 180;
////                        float rotatedX = (float) (x * (Math.cos(Math.toRadians(angleDiff)) - Math.sin(Math.toRadians(angleDiff))));
////                        float rotatedY = (float) (y * (Math.sin(Math.toRadians(angleDiff)) + Math.cos(Math.toRadians(angleDiff))));
//
//                        double rotatedX = x;
//                        double rotatedY = y;
//
//                        double latitude = rotatedY * latScaleFactor + originLat;
//                        double longitude = rotatedX * lngScaleFactor + originLng;
//
//                        // set location to update mapxus map and wait for interval update
//                        queuedLocation = new com.mapxus.map.mapxusmap.positioning.IndoorLocation("TURTLE_BOT",
//                                latitude, longitude, "3F", "31742af5bc8446acad14e0c053ae468a", System.currentTimeMillis());
//                        locUpdateInQueue = true;
//
//                        // update orientation
//                        if (mapxusPositioningProvider != null)
//                            mapxusPositioningProvider.dispatchCompassChange((float) orientation, com.mapxus.map.mapxusmap.positioning.SensorAccuracy.SENSOR_ACCURACY_MEDIUM);
//
//                    }
//                }
//            }
//
//
//
//
//
////            if (data.getMessage() instanceof PoseWithCovarianceStamped) {
////                PoseWithCovarianceStamped pose = (PoseWithCovarianceStamped) data.getMessage();
////
//////                if (data.getTopic().name.equals(CustomTopicName.SLOVLP_EKF.name)) {
////                if (data.getTopic().name.equals("/odom")) {
////                    double x = pose.getPose().getPose().getPosition().getX();
////                    double y = pose.getPose().getPose().getPosition().getY();
////
////                    Log.d(TAG, "vlpekf topic: " + data.getTopic().name + data.getTopic().type);
////                    Log.d(TAG, "vlpekf data (x,y): " + x + ", " + y);
////
////                    // display the icon on map if the MAP IS READY
////                    if (mapboxMap != null && mapxusMap != null) {
////                        // CYT building corner as origin
////                        double originLat = 22.334566;
////                        double originLng = 114.263432;
////
////                        // simple way to calculate
////                        final float latScaleFactor = (float) 0.00001;
////                        final float lngScaleFactor = (float) 0.00001045;
////
////                        // TODO: simplify this by calculating rotatedX & rotatedY once only (per venue)
//////                        int angleDiff = 180;
//////                        float rotatedX = (float) (x * (Math.cos(Math.toRadians(angleDiff)) - Math.sin(Math.toRadians(angleDiff))));
//////                        float rotatedY = (float) (y * (Math.sin(Math.toRadians(angleDiff)) + Math.cos(Math.toRadians(angleDiff))));
////
////                        double rotatedX = -x;
////                        double rotatedY = -y;
////
////                        double latitude = rotatedY * latScaleFactor + originLat;
////                        double longitude = rotatedX * lngScaleFactor + originLng;
////
////                        // set location to update mapxus map and wait for interval update
////                        queuedLocation = new com.mapxus.map.mapxusmap.positioning.IndoorLocation("TURTLE_BOT",
////                                latitude, longitude, "3F", "31742af5bc8446acad14e0c053ae468a", System.currentTimeMillis());
////                        locUpdateInQueue = true;
////
////                    }
////                }
////            }
//
//            Log.d(TAG, "general data topic: " + data.getTopic().name + data.getTopic().type);
//            Log.d(TAG, "general data message: " + data.getMessage());
//
////            if (data.getMessage() instanceof TFMessage) {
////                Log.d(TAG, "tf data topic: " + data.getTopic().name + data.getTopic().type);
////                Log.d(TAG, "tf data message: " + data.getMessage().toRawMessage().getName());
////                Log.d(TAG, "tf data message: " + data.getMessage().toRawMessage().getType());
////            }
//        });

        return v;
    }

    @Override
    public void onMapReady(@NonNull @NotNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        // move camera to ICDC
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new com.mapbox.mapboxsdk.geometry.LatLng(22.33446, 114.263551), 18), 500);

        mapboxMap.getStyle(style -> {
            // prepareMapboxLocationComponent
            mapBoxDefaultLocationComponentOptions = LocationComponentOptions.builder(getActivity())
                    .elevation(6)
                    .backgroundTintColor(Color.WHITE)
                    .build();
            mapBoxDefaultLocationComponentActivationOptions = LocationComponentActivationOptions.builder(getActivity(), style)
                    .locationComponentOptions(mapBoxDefaultLocationComponentOptions)
                    .build();
            mapBoxCustomLocationOptions = LocationComponentOptions.builder(getActivity())
                    .elevation(0)
                    .accuracyAlpha(0.3f)
                    .enableStaleState(false)
                    .foregroundTintColor(Color.parseColor("#00000000"))
                    //.foregroundDrawable(R.drawable.transparent_circle)
                    .backgroundTintColor(Color.parseColor("#00000000"))
                    //.backgroundDrawable(R.drawable.transparent_circle)
                    //.foregroundTintColor(Color.parseColor("#e1c03a"))
                    //.backgroundTintColor(Color.parseColor("#19b1dc"))
                    //.backgroundTintColor(Color.parseColor("#e1c03a"))
                    .build();
            mapBoxCustomLocationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getActivity(), style)
                            .locationComponentOptions(mapBoxCustomLocationOptions)
                            .build();
            //mapBoxLocationComponent = mapboxMap.getLocationComponent();
            //mapBoxLocationComponent.activateLocationComponent(customlocationComponentActivationOptions);
            //mapBoxLocationComponent.activateLocationComponent(mapBoxDefaultLocationComponentActivationOptions);

            // map marker manager
            symbolManager = new SymbolManager(mapView, mapboxMap, style);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setIconIgnorePlacement(true);
            symbolManager.setIconOptional(false);
        });

//        mapboxMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//
//                // show camera coordinate in developer mode
//
//                LatLng latLng = mapboxMap.getCameraPosition().target;
//
//                String latString = String.format("%.6f", latLng.getLatitude());
//                String lngString = String.format("%.6f", latLng.getLongitude());
//
//                double bearing = mapboxMap.getCameraPosition().bearing;
//                double zoomLevel = mapboxMap.getCameraPosition().zoom;
//
//                String message = String.format("Camera: %s,%s\nBearing: %f Zoom lv: %f",
//                        latString, lngString, bearing, zoomLevel);
//
//            }
//        });
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mapxusPoseViewGroup = view.findViewById(R.id.mapxusView);
//        mapxusPoseViewGroup.setDataListener(this);
//        mapxusPoseViewGroup.setOnWidgetDetailsChanged(this);
    }

    @Override
    public void onStart() {
        super.onStart();
//        startLocationUpdateThread();
        mViewModel.onStart();
//        mViewModel.getMapxusManager().onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.onPause();
//        mViewModel.getMapxusManager().onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.onDestroy();
    }

    @Override
    public void onDestroyView() {
        System.gc();
        super.onDestroyView();
    }

    @Override
    public void onMapxusMapReady(MapxusMap mapxusMap) {
        symbolManager.addClickListener(onMapboxMarkerClickListener);

        this.mapxusMap = mapxusMap;

        /*
        // test for movable icon
        // removeMarker() in the loop causing fatal signal 6
         */
//        LatLng robotPos = new LatLng(22.334499, 114.263551);
//        MapxusMarkerOptions robotMarkerOption = new MapxusMarkerOptions()
//                .setPosition(robotPos)
//                .setFloor("3F")
//                .setBuildingId("31742af5bc8446acad14e0c053ae468a")
//                .setIcon(R.drawable.robot7);
//
//        MapxusMarker robotMarker = mapxusMap.addMarker(robotMarkerOption);
//
//        for (int i = 1; i < 10; i++) {
//            mapxusMap.removeMarker(robotMarker);
//
//            // reset position
//            LatLng pos = new LatLng(robotPos.latitude - 0.000005, robotPos.longitude - 0.000005);
//            robotMarkerOption.setPosition(pos);
//
//            // change latlng
//            mapxusMap.addMarker(robotMarkerOption);
//        }

//        MapxusMarkerOptions LiPHYMarker = new MapxusMarkerOptions()
//                .setPosition(new com.mapxus.map.mapxusmap.api.map.model.LatLng(22.334469, 114.263424))
//                .setFloor("3F")
//                .setBuildingId("31742af5bc8446acad14e0c053ae468a")
//                .setIcon(R.drawable.lightbulb10);

        // indoor map click listener
        mapxusMap.addOnMapClickListener((latLng, floor, buildingId, s2) -> {
            String latString = String.format("%.6f", latLng.latitude);
            String lngString = String.format("%.6f", latLng.longitude);

            Timber.tag("map click").d("lat: %s, lng: %s", latString, lngString);
            Timber.tag("map click").d("floor: %s, buildingId: %s", floor, buildingId);

            // logics for showing icon
//            if (LiphyState.isDeveloperMode()) {
//                if (debugCoordinate.getVisibility() != View.VISIBLE) {
//                    debugCoordinate.setVisibility(View.VISIBLE);
//                }
//
//                debugCoordinate.setText(message);
//
//                // clear the old dot if any
//                if (debugDot != null)
//                    mapxusMap.removeMarker(debugDot);
//                else
//                    showDebugToast("debugDot is null");
//
//                // draw dot
////                MapxusMarkerOptions options = new MapxusMarkerOptions()
////                debugDot = mapxusMap.addMarker(options);
//
//            } else {
//                if (debugCoordinate.getVisibility() != View.GONE) {
//                    debugCoordinate.setVisibility(View.GONE);
//                }
//
//                if (debugDot != null)
//                    mapxusMap.removeMarker(debugDot);
//                else
//                    showDebugToast("debugDot is null");
//            }

        });


        // init positioing provider
        mapxusPositioningProvider = new MapxusNavigationPositioningProvider(requireActivity(), requireActivity().getApplicationContext());
        mapxusPositioningProvider.addListener(new IndoorLocationProviderListener() {
            @Override
            public void onProviderStarted() {
                String name = mapxusPositioningProvider.getName();
            }

            @Override
            public void onProviderStopped() {
//                isMapReadyForCompass = true;
                //mapxusMap.setLocationProvider(mapxusPositioningProvider);
                //mapxusPositioningProvider.start();
            }

            @Override
            public void onProviderError(com.mapxus.map.mapxusmap.positioning.ErrorInfo errorInfo) {
            }

            @Override
            public void onIndoorLocationChange(com.mapxus.map.mapxusmap.positioning.IndoorLocation indoorLocation) {
            }

            @Override
            public void onCompassChanged(float v, int i) {
            }
        });

        // init mapxus manager
        mViewModel.getMapxusManager().init(mapxusPositioningProvider);
        Timber.tag(TAG).d("init mapxus manager");

        mapxusMap.setLocationProvider(mapxusPositioningProvider);

    }

    @Override
    public void OnTemiLocationChanged(IndoorLocation location) {
        Timber.tag(TAG).d("temi location update: %f, %f", location.getLatitude(), location.getLongitude());
        updateLocationOnMap(location);

//        // set location to update mapxus map and wait for interval update
//        queuedLocation = location;
//        locUpdateInQueue = true;
//        if (mapxusPositioningProvider != null)
//            mapxusPositioningProvider.dispatchCompassChange(degree, com.mapxus.map.mapxusmap.positioning.SensorAccuracy.SENSOR_ACCURACY_MEDIUM);
    }

    @Override
    public void OnRobotLocationChanged(IndoorLocation location) {
        Timber.tag(TAG).d("robot location update: %f, %f", location.getLatitude(), location.getLongitude());
        updateLocationOnMap(location);
    }

    private void updateLocationOnMap(IndoorLocation location) {
        if (mapxusMap == null) {
            Timber.tag(TAG).d("mapxusMap is null, cannot update location");
            return;
        }

        // update location
        mViewModel.getMapxusManager().updateQueuedLocation(location);

        // update orientation
        mViewModel.getMapxusManager().dispatchCompassChange(location.getBearing());
    }

    //    @Override
//    public void onNewWidgetData(BaseData data) {
////        mViewModel.publishData(data);
//    }
//
//    @Override
//    public void onWidgetDetailsChanged(BaseEntity widgetEntity) {
//        mViewModel.updateWidget(widgetEntity);
//    }

//    private final Runnable locationUpdateRunnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                if(locUpdateInQueue) {
//                    mapxusPositioningProvider.dispatchIndoorLocationChange(queuedLocation);
//                }
//            } finally {
//                // 100% guarantee that this always happens, even if
//                // your update method throws an exception
//                locationUpdateHandler.postDelayed(locationUpdateRunnable, 200);
//            }
//        }
//    };
//
//    private void startLocationUpdateThread() {
//        locationUpdateHandler = new Handler();
//        locationUpdateRunnable.run();
//    }
//
//    private void stopLocationUpdateThread() {
//        locationUpdateHandler.removeCallbacks(locationUpdateRunnable);
//    }


}

