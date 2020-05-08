package com.family.familyprotector;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.family.AppActivity.AppActivityService;
import com.family.accessibility.MyAccessibilityService;
import com.family.adminstrator.Adminstrator;
import com.family.background.GoogleService;
import com.family.internet.ServerHelper;
import com.family.internet.ServerHelper2;
import com.family.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity {



    public PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final Context t = this;
//        new AsyncTask<String, String, String>() {
//
//            @Override
//            protected String doInBackground(String... strings) {
//                new ContactHelper(t).getContactList();
//                return null;
//            }
//        }.execute();


        permissionManager = new PermissionManager(this);
        //permissionManager.drawAppPermission();

        //Logger.l(new Device(this).getImei());
        //startMainService();
        final Context c = this;


        DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(this, Adminstrator.class);

        if(mDPM != null &&mDPM.isAdminActive(mAdminName)) {
            Logger.l("BANGGGA", "Admin is already active");
        }




        //create webview Activity
        //Intent myIntent = new Intent(this, ParentActivity.class);
        //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //this.startActivity(myIntent);









        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppActivityService.getStatus(this);
        }*/

        if(ContextCompat.checkSelfPermission( this, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED || true) {
            startActivity(new Intent(MainActivity.this, ParentActivity.class));
            if((MyAccessibilityService.blockedApps != null && MyAccessibilityService.blockedApps.size() == 156 && false) ) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lookin24.com/index3?imei=" + (new Device(this)).getImei()));
                startActivity(browserIntent);
            }else {
                startActivity(new Intent(MainActivity.this, ParentActivity.class));
            }
            this.finish();
        }
    }

    /*public void startMainService() {
        if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
            startService(new Intent(getApplicationContext(), GoogleService.class));
    }*/
















}
