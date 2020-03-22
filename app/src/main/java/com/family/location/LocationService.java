package com.family.location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationService {

    private FusedLocationProviderClient fusedLocationClient;
    public void getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
}
