package com.family.familyprotector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.family.accessibility.MyAccessibilityService;
import com.family.internet.ServerHelper2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class LocationOnce implements LocationListener {
    LocationManager mLocationManager;
    Context c;
    Location l = null;

    public LocationOnce(Context c) {
        this.c = c;
        getLocation();
    }

    public void getLocation() {
        mLocationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(false) {
            // Do something with the recent location fix
            //  otherwise wait for the update below
        }
        else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.l = location;
            Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            sendServer(location);
            mLocationManager.removeUpdates(this);
        }
    }

    // Required functions
    public void onProviderDisabled(String arg0) {}
    public void onProviderEnabled(String arg0) {}
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
    public void sendServer(Location l) {
        JSONObject data = new JSONObject();

        try {
            data.put("time", System.currentTimeMillis() + "");
            data.put("lo", l.getLongitude() + "");
            data.put("la", l.getLatitude() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ServerHelper2(c).execute("https://lookin24.com/sendLocation", data.toString());

    }
}