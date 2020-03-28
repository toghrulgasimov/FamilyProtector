package com.family.familyprotector;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.family.accessibility.MyAccessibilityService;
import com.family.adminstrator.Adminstrator;

import java.util.List;
import java.util.Set;

public class PermissionManager {
    public Activity activity;
    final int DRAW = 0, USSAGE = 1, BATTERY = 2, NOTIFICATION = 3, ACCESIBILITY = 4, ADMIN = 5, SIMPLE = 6;
    public PermissionManager(Activity activity) {
        this.activity = activity;
    }
    public void askSimplePermissions() {
        String[] p = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET,
                Manifest.permission.KILL_BACKGROUND_PROCESSES, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS
        };
        ActivityCompat.requestPermissions(this.activity,
                p,
                SIMPLE);
    }
    public void activityResult(int requestCode) {
        if(requestCode == DRAW) {
            setUssage();
        }else if(requestCode == USSAGE) {
            setBattery();
        }else if(requestCode == BATTERY) {
            setNotificationAccess();
        }else if(requestCode == NOTIFICATION) {
            setAdmin();
        }else if(requestCode == ADMIN) {
            askSimplePermissions();
        }else if(requestCode == ACCESIBILITY) {

        }
    }

    public boolean isUssageS() {
        AppOpsManager appOps = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            appOps = (AppOpsManager) activity
                    .getSystemService(Context.APP_OPS_SERVICE);
        }
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), activity.getPackageName());
        }
        return (mode == AppOpsManager.MODE_ALLOWED);
    }
    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }


    public void setUssage() {
        if(!isUssageS()) {
            activity.startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), USSAGE);
        }else {
        }
    }
    public void setBattery() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(activity.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivityForResult(intent, BATTERY);
            }
        }
    }
    public void setNotificationAccess() {



        String packageName = activity.getPackageName();
        Set<String> enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(activity);
        if(enabledPackages.contains(packageName)) {

        }else {
            activity.startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), NOTIFICATION);
//            getApplicationContext().startActivity(new Intent(
//                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }
    public void setAccesibiltyOn() {
        if(!this.isAccessibilityServiceEnabled(activity, MyAccessibilityService.class)) {
            Intent openSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            activity.startActivityForResult(openSettings, ACCESIBILITY);
            Logger.l("not Enabled");
        }else {
            Logger.l(" Enabled");
        }

    }
    public void setAdmin() {
        //https://developer.android.com/guide/topics/admin/device-admin
        //https://stackoverflow.com/questions/30130163/enable-device-admin-dialog-not-showing
        DevicePolicyManager mDPM = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdmin = new ComponentName(activity, Adminstrator.class);

        if(mDPM != null &&mDPM.isAdminActive(mDeviceAdmin)) {
            Log.d("salam", "Active");
        }else {
            Log.d("salam", "Not Active");

            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "EXPLANATION");
            activity.startActivityForResult(intent, ADMIN);
        }
    }
    public void drawAppPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivityForResult(intent, DRAW);
            }
        }
    }
}