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
import com.family.internet.ServerHelper2;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Logger.l("Refreshed token: " + token);

        //send server
        postStokenJSON(token);
    }
    public void postStokenJSON(String token) {
        JSONObject postData = new JSONObject();
        Log.d("posted", "posJson from Firebase");
        String ts = Context.TELEPHONY_SERVICE;
        String imei = new Device(this).getImei();
        try {
            postData.put("t", token);
            postData.put("i", imei);
            new ServerHelper(this).execute("http://tmhgame.tk/updateFirebaseToken", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void postActJSON(JSONObject o) {


        try {
            o.put("imei", new Device(this).getImei());
            Logger.l( " That will be post" + o.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ServerHelper(this).execute("http://tmhgame.tk/sendActivity", o.toString());
    }
    public void postYActJSON(JSONObject o) {


        try {
            o.put("imei", new Device(this).getImei());
            Logger.l( " That will be post" + o.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ServerHelper2(this).execute("http://tmhgame.tk/sendYoutube", o.toString());
    }
    public void postWActJSON(JSONObject o) {
        try {
            o.put("imei", new Device(this).getImei());
            Logger.l( " That will be post for WebSites " + o.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ServerHelper2(this).execute("http://tmhgame.tk/sendWebSites", o.toString());
    }
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Logger.l("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        Logger.l( message.getData().toString() + "ALDIM");
        Map M = message.getData();


        if(M.get("command") != null && M.get("command").equals("sendActivity")) {
            JSONObject data = new JSONObject();
            JSONArray ar = new JSONArray();
            for(MyAccessibilityService.Ac a: MyAccessibilityService.activities) {
                JSONObject o = new JSONObject();
                try {
                    o.put("package", a.pa);
                    //String nn =
                    String nn = new Device(this).getAppName(a.pa);
                    if(nn.equals("")) nn = "Menu";
                    o.put("name", nn);
                    o.put("start", a.start+"");
                    o.put("end", a.end+"");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ar.put(o);
            }
            try {
                data.put("data", ar);
                postActJSON(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(M.get("command") != null && M.get("command").equals("sendYoutube")) {
            JSONObject data = new JSONObject();
            JSONArray ar = new JSONArray();
            for(MyAccessibilityService.YAc a: MyAccessibilityService.yactivities) {
                JSONObject o = new JSONObject();
                try {
                    o.put("name", a.name);
                    //String nn =
                    o.put("start", a.time+"");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ar.put(o);
            }
            try {
                data.put("data", ar);
                postYActJSON(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else  if(M.get("command") != null && M.get("command").equals("sendWebsites")) {
            JSONObject data = new JSONObject();
            JSONArray ar = new JSONArray();
            for(MyAccessibilityService.WAc a: MyAccessibilityService.webSites) {
                JSONObject o = new JSONObject();
                try {
                    o.put("url", a.url);
                    //String nn =
                    o.put("start", a.time+"");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ar.put(o);
            }
            try {
                data.put("data", ar);
                postWActJSON(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            String p = (String)M.get("package");
            String b = (String)M.get("block");
            Logger.l(p + " bunun uzerinde emeliyyat");
            //MyAccessibilityService.instance.sondur();
            if(b.equals("0")) {
                MyAccessibilityService.blockedApps.remove(p);
            }else {
                MyAccessibilityService.blockedApps.add(p);
            }

            Logger.l("Sondur cagrildi");
        }

    }
}
