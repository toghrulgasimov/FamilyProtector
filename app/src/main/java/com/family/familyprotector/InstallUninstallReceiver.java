package com.family.familyprotector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import com.family.internet.InternetHelper;
import com.family.util.Util;

import org.json.JSONObject;

import java.io.File;

public class InstallUninstallReceiver extends BroadcastReceiver {
    public Context context;
    public InstallUninstallReceiver(Context c) {
        context = c;
    }
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

                    u.uploadImage("", new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//"+pname+".png"));
                    JSONObject jo = new JSONObject();
                    try{
                        Device d = new Device(context);
                        jo.put("imei", d.getImei());
                        jo.put("p", pname);
                        jo.put("n", d.getAppName(pname));
                        new InternetHelper().send("http://lookin24.com/addApp", jo.toString());
                    }catch (Exception e){}

                    return null;
                }
            }.execute();


        }else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            //remove from user apps
        }

    }
}
