package com.johnnyhoichuen.rosandroid;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapxus.map.mapxusmap.api.map.MapxusMapContext;

import timber.log.Timber;

public class App extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MapxusMapContext.init(getApplicationContext());
        Mapbox.getInstance(getApplicationContext(), "/Iy3eV0lc");

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
