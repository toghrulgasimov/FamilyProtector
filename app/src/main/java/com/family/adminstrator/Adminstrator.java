package com.family.adminstrator;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Adminstrator extends DeviceAdminReceiver {
    //https://github.com/mitre/device-admin-sample/blob/master/app/src/main/AndroidManifest.xml
    // i searched there is no need to declare in Manifest
    //https://stackoverflow.com/questions/17943300/how-to-know-if-my-application-admin-or-not/17943666


    @Override
    public void onEnabled(Context context, Intent intent) {
        //Log.i(this, "admin_receiver_status_enabled");
        // admin rights
        //App.getPreferences().edit().putBoolean(App.ADMIN_ENABLED, true).commit(); //App.getPreferences() returns the sharedPreferences

    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "admin_receiver_status_disable_warning";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        //Log.info(this, "admin_receiver_status_disabled");
        // admin rights removed
        //App.getPreferences().edit().putBoolean(App.ADMIN_ENABLED, false).commit(); //App.getPreferences() returns the sharedPreferences
    }
}
