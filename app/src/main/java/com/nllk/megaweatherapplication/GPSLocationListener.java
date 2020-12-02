package com.nllk.megaweatherapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GPSLocationListener implements LocationListener {

    public static Location currentLocation; // здесь будет всегда доступна самая последняя информация о местоположении пользователя.

    public static String providerStatus;

    public static void SetUpLocationListener(Context context) // это нужно запустить в самом начале работы программы
    {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new GPSLocationListener();

        if (context.checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
            //return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10,
                locationListener); // здесь можно указать другие более подходящие вам параметры

        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
    @Override
    public void onLocationChanged(Location loc) {
        currentLocation = loc;
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        providerStatus = "Provider Disabled";
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        providerStatus = "Provider Enabled";
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        providerStatus = "Something wrong i can feel it. Stasus: " + status;
    }
}