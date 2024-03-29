package com.family.familyprotector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import com.family.accessibility.MyAccessibilityService;
import com.family.internet.InternetHelper;
import com.family.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class InstallUninstallReceiver extends BroadcastReceiver {


    public static ArrayList<String> mustBeRemoved = new ArrayList<>();
    @Override
    public void onReceive(final Context context, Intent intent) {
        String[] ar = intent.getDataString().split(":");
        final String pname = ar[1];
        Logger.l("INSTALLAPPP", "Install " + intent.getAction() + "--" + intent.getData());
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            //upload icon to server
            new FileR(context).writeIcon(pname);
            Logger.l("INSTALLAPPP", "Yazildi");
            final Util u = new Util(context);
            new AsyncTask<String, String, Void>() {

                @Override
                protected Void doInBackground(String... strings) {

                    u.uploadImage("", new File(Environment.getExternalStorageDirectory() + "//.FamilyProtector//"+pname+".png"));
                    JSONObject jo = new JSONObject();
                    try{
                        Device d = new Device(context);
                        jo.put("imei", d.getImei());
                        jo.put("p", pname);
                        jo.put("n", d.getAppName(pname));
                        new InternetHelper().send("http://lookin24.com/addApp", jo.toString());
                        if(MyAccessibilityService.Apps != null)
                            MyAccessibilityService.Apps.add(pname);
                    }catch (Exception e){}

                    return null;
                }
            }.execute();


        }else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            //remove from user apps
            Logger.l("INSTALLAPPP", "Girdi removaya");
            if(MyAccessibilityService.Apps != null) {
                MyAccessibilityService.Apps.remove(pname);
                mustBeRemoved.add(pname);
            }
            new AsyncTask<String, String, Void>() {

                @Override
                protected Void doInBackground(String... strings) {
                    JSONObject jo = new JSONObject();

                    try{
                        Device d = new Device(context);
                        jo.put("imei", d.getImei());
                        JSONArray jar = new JSONArray();
                        for(String x : mustBeRemoved) {
                            jar.put(x);
                        }
                        jo.put("ar", jar);
                        Logger.l("INSTALLAPPP", "Gonderilir");
                        String ans = new InternetHelper().send("http://lookin24.com/removeApp", jo.toString());
                        Logger.l("INSTALLAPPP", "Gonderildi");
                        if(ans.equals("1")) {
                            mustBeRemoved.clear();
                            Logger.l("INSTALLAPPP", "mustremove temizlendi");
                        }
                    }catch (Exception e){}

                    return null;
                }
            }.execute();
        }

    }
}
