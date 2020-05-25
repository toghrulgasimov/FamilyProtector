package com.family.familyprotector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.family.accessibility.MyAccessibilityService;
import com.family.internet.ServerHelper2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Device {



    public Context context;

    public Device(Context c) {
        this.context = c;
    }

    public String getAppName(String name) {
        final PackageManager pm = context.getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( name, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "");
        return applicationName;
    }

    public String getImeiold() {
        String ans = null;
        String ts = Context.TELEPHONY_SERVICE;
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(ts);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "erorrororororroro";
        }else {
            ans =  mTelephonyMgr.getDeviceId();
        }
        return ans;
    }
    public static String deviceId = null;
    public String getImei() {
        if(deviceId != null) return deviceId;

        File file = new File(Environment.getExternalStorageDirectory() + "//.FamilyProtector//ids.txt");
        String ans = "";
        SharedPreferences sp = null;
        if(MyAccessibilityService.instance != null) {
            sp = MyAccessibilityService.instance.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        }
        if(sp != null && sp.contains("id")) {
            String a = sp.getString("id", null);
            if(a != null) {
                try{
                    if(!file.exists()) {
                        Logger.l("BANGE", "Shared Preferenceden fayla yazildi");
                        FileR.checkFolder();
                        new FileR(context).write("ids.txt", a, false);
                    }
                }catch (Exception e){}

                ans = a;
            }
        }
        if((ans == null || ans.equals("")) && file.exists()) {
            try {
                ans = new FileR(context).read("ids.txt");
                if(sp == null) {
                    sp = ParentActivity.that.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
                }
                SharedPreferences.Editor e = sp.edit();
                e.putString("id", ans);
                e.commit();
                Logger.l("BANGE", "Shared Preference yazildi");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if((ans == null || ans.equals(""))){
            ans = UUID.randomUUID().toString();
            try {
                Logger.l("BANGE", "Hem shared preference hemde Fayla yazildi");
                FileR.checkFolder();
                new FileR(context).write("ids.txt", ans, false);
                if(sp == null) {
                    sp = ParentActivity.that.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
                }
                SharedPreferences.Editor e = sp.edit();
                e.putString("id", ans);
                e.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//b11fc6cf-ca12-4c2b-b76f-ef679b845847
        Logger.l("DEVICEID", ans);
        return ans;
    }
    public Set<String> getApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities( mainIntent, 0);
        Set<String> ans = new HashSet<>();
        for(ResolveInfo x : pkgAppsList) {
            if(x.activityInfo.packageName.equals("com.android.systemui"))
                continue;
            ans.add(x.activityInfo.packageName);
        }
        return ans;
    }
}
