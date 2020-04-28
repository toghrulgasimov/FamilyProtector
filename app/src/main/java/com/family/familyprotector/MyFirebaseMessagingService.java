package com.family.familyprotector;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.family.accessibility.MyAccessibilityService;
import com.family.background.GoogleService;
import com.family.internet.InternetHelper;
import com.family.internet.ServerHelper;
import com.family.internet.ServerHelper2;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    LocationOnce L = null;
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
            new ServerHelper(this).execute("https://lookin24.com/updateFirebaseToken", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public  void sendAct() {
        JSONObject data = new JSONObject();
        JSONArray ar = new JSONArray();
        Logger.l("Send Activity cagrildi");
        if(MyAccessibilityService.activities == null) {
            return;
        }
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
    }
    public void sendYoutube() {
        JSONObject data = new JSONObject();
        JSONArray ar = new JSONArray();
        if(MyAccessibilityService.yactivities == null) {
            return;
        }
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
    }
    public void sendWeb() {
        JSONObject data = new JSONObject();
        JSONArray ar = new JSONArray();
        if(MyAccessibilityService.webSites == null) {
            return;
        }
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
    }
    public void postActJSON(final JSONObject o) {


        try {
            o.put("imei", new Device(this).getImei());
            Logger.l( " That will be post" + o.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //new ServerHelper2(this).execute("https://lookin24.com/sendActivity", o.toString());
        new AsyncTask<String, String, Void>() {

            @Override
            protected Void doInBackground(String... strings) {
                String ans = new InternetHelper().send("https://lookin24.com/sendActivity", o.toString());
                if(ans.equals("1")) {
                    if(MyAccessibilityService.activities.size() > 0) {
                        MyAccessibilityService.Ac l = MyAccessibilityService.activities.get(MyAccessibilityService.activities.size()-1);
                        MyAccessibilityService.activities.clear();
                        MyAccessibilityService.activities.add(l);
                    }
                }
                Logger.l(ans + " sendActivitydan cavabdir");
                return null;
            }
        }.execute();
    }
    public void postYActJSON(final JSONObject o) {


        try {
            o.put("imei", new Device(this).getImei());
            Logger.l( " That will be post" + o.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //new ServerHelper2(this).execute("https://lookin24.com/sendYoutube", o.toString());
        new AsyncTask<String, String, Void>() {

            @Override
            protected Void doInBackground(String... strings) {
                String ans = new InternetHelper().send("https://lookin24.com/sendYoutube", o.toString());
                if(ans.equals("1")) {
                    if(MyAccessibilityService.yactivities.size() > 0) {
                        MyAccessibilityService.YAc last = MyAccessibilityService.yactivities.get(MyAccessibilityService.yactivities.size()-1);
                        MyAccessibilityService.yactivities.clear();
                        MyAccessibilityService.yactivities.add(last);
                    }

                }
                Logger.l(ans + " sendYoutubeden cavabdir");
                return null;
            }
        }.execute();
    }
    public void postWActJSON(final JSONObject o) {
        try {
            o.put("imei", new Device(this).getImei());
            Logger.l( " That will be post for WebSites " + o.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //new ServerHelper2(this).execute("https://lookin24.com/sendWebSites", o.toString());
        new AsyncTask<String, String, Void>() {

            @Override
            protected Void doInBackground(String... strings) {
                String ans = new InternetHelper().send("https://lookin24.com/sendWebSites", o.toString());
                if(ans.equals("1")) {
                    if(MyAccessibilityService.webSites.size() > 0) {
                        MyAccessibilityService.WAc last = MyAccessibilityService.webSites.get(MyAccessibilityService.webSites.size()-1);
                        MyAccessibilityService.webSites.clear();
                        MyAccessibilityService.webSites.add(last);
                    }
                }
                Logger.l(ans + " senWebden cavabdir");
                return null;
            }
        }.execute();
    }

    public MyFirebaseMessagingService() {
        final Handler h = new Handler();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        new AsyncTask<String, String, Void>() {
                            @Override
                            protected Void doInBackground(String... strings) {
                                if(MyAccessibilityService.activities == null) {
                                    return null;
                                }
                                if(MyAccessibilityService.activities.size() > 1) {
                                    //sendAct();
                                }
                                if(MyAccessibilityService.webSites.size() > 1) {
                                    sendWeb();
                                }
                                if(MyAccessibilityService.yactivities.size() > 1) {
                                    sendYoutube();
                                }

                                Logger.l("Activity sended");
                                return null;
                            }
                        }.execute();
                    }
                });
            }
        }, 0, 1000 * 60 * 20);
    }
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Logger.l("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        Logger.l( message.getData().toString() + "ALDIM");
        Map M = message.getData();



        if(M.get("command") != null && M.get("command").equals("sendActivity")) {
            sendAct();
        }else if(M.get("command") != null && M.get("command").equals("sendYoutube")) {
            sendYoutube();
        }else  if(M.get("command") != null && M.get("command").equals("sendWebsites")) {
            sendWeb();
        }else  if(M.get("command") != null && M.get("command").equals("sendLocation")) {
            GoogleService.sendNow = true;
        }else if(M.get("command") != null && M.get("command").equals("sendWhatsapp")) {
            GoogleService.sendWhatsapp = true;
        }else{
            String p = (String)M.get("package");
            String b = (String)M.get("block");
            if(p == null) return;
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
