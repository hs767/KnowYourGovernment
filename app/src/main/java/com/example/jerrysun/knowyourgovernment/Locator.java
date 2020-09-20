package com.example.jerrysun.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by jerrysun on 4/30/17.
 */

public class Locator {

    private MainActivity owner;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public Locator(MainActivity mainActivity) {
        this.owner = mainActivity;
        if (checkPermission()) {
            setUpLocationManager();
            determineLocation();
        }
    }

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(owner, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(owner,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            return false;
        }

        return true;
    }

    public void setUpLocationManager() {

        if (locationListener != null)
            return;

        if (!checkPermission())
            return;

        locationManager = (LocationManager) owner.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //do nothing
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    public void determineLocation() {
        if (!checkPermission()) {return;}

        if (locationManager == null) {setUpLocationManager();}

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                owner.doLocationWork(loc.getLatitude(), loc.getLongitude());
            }
        }

        owner.noLocationAvailable();
    }

    public void shutdown() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }
}
