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
import com.mapxus.map.mapxusmap.api.map.model.LatLng;
import com.mapxus.map.mapxusmap.api.services.RoutePlanning;
import com.mapxus.map.mapxusmap.impl.MapboxMapViewProvider;
import com.mapxus.map.mapxusmap.positioning.IndoorLocation;
import com.mapxus.map.mapxusmap.positioning.IndoorLocationProviderListener;
import com.johnnyhoichuen.rosandroid.R;
import com.johnnyhoichuen.rosandroid.mapxus.MapxusNavigationPositioningProvider;
import com.johnnyhoichuen.rosandroid.viewmodel.MapxusViewModel;
import org.jetbrains.annotations.NotNull;

public class MapxusFragment extends Fragment implements OnMapReadyCallback, OnMapxusMapReadyCallback,
    MapxusViewModel.OnTemiLocationChangedListener, MapxusViewModel.OnRobotLocationChangedListener {

    private static final String TAG = MapxusFragment.class.getSimpleName();

    private MapxusViewModel mViewModel;

    // map basics
    private MapView mapView;
    private MapViewProvider mapViewProvider;
    private MapboxMap mapboxMap;
    private MapxusMap mapxusMap;

    // map navigation
    private MapxusNavigationPositioningProvider mapxusPositioningProvider;

    // map marker
    private SymbolManager symbolManager;
    private OnSymbolClickListener onMapboxMarkerClickListener;
    private LocationComponentOptions mapBoxDefaultLocationComponentOptions;
    private LocationComponentActivationOptions mapBoxCustomLocationComponentActivationOptions;
    private LocationComponentActivationOptions mapBoxDefaultLocationComponentActivationOptions;
    private LocationComponentOptions mapBoxCustomLocationOptions;

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

        return v;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.onPause();
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
    public void onMapReady(@NonNull @NotNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        // move camera to ICDC
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new com.mapbox.mapboxsdk.geometry.LatLng(22.33446, 114.263551),
            18.5), 500);

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

            Timber.tag("mapclick").d("lat: %s, lng: %s", latString, lngString);
            Timber.tag("mapclick").d("floor: %s, buildingId: %s", floor, buildingId);

            // send latlng to view model
            // handle single floor for now
            // translate to temi's x'y
            mViewModel.onMapClicked(latLng);

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

        // update mapxus map with positioning provider
        mapxusMap.setLocationProvider(mapxusPositioningProvider);

    }

    @Override
    public void OnTemiLocationChanged(IndoorLocation location) {
        Timber.tag(TAG).d("temi location update: %f, %f", location.getLatitude(), location.getLongitude());
        updateLocationOnMap(location);
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
}

