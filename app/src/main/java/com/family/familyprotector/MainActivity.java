package com.family.familyprotector;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.family.AppActivity.AppActivityService;
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

        //new ServerHelper(this).execute("https://lookin24.com/sendActivity", "sadasdasdasdasd");


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
        permissionManager.drawAppPermission();

        Logger.l(new Device(this).getImei());
        startMainService();
        final Context c = this;




        //create webview Activity
        //Intent myIntent = new Intent(this, ParentActivity.class);
        //myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //this.startActivity(myIntent);









        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppActivityService.getStatus(this);
        }

        if(ContextCompat.checkSelfPermission( this, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(MainActivity.this, ParentActivity.class));
            this.finish();
        }
    }

    public void startMainService() {
        if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
            startService(new Intent(getApplicationContext(), GoogleService.class));
    }


    public void checkFolder() throws IOException {
        File mFolder = new File(Environment.getExternalStorageDirectory(), "FamilyProtector");
        if (!mFolder.exists()) {
            mFolder.mkdirs();
            mFolder.setExecutable(true);
            mFolder.setReadable(true);
            mFolder.setWritable(true);
        }
        File file = new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//blockedapps.txt");
        if(!file.exists()) {
            file.createNewFile();
            Log.d("file", "created");
        }else {
            Log.d("file", "exist");
        }
        file = new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//locations.txt");
        if(!file.exists()) {
            file.createNewFile();
        }

    }
    public void postJSONFirebase(String token) {
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





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final Context c = this;
        startService(new Intent(getApplicationContext(), GoogleService.class));
        permissionManager.setAccesibiltyOn();
        new AsyncTask<Integer, Integer, Void>() {

            @Override
            protected Void doInBackground(Integer... integers) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("TAG", "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                postJSONFirebase(token);
                                Logger.l("Token- " + token);
                                Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                            }
                        });

                try {
                    checkFolder();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    new FileR().write("locations.txt", "123.1231:123.3213", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    firstTimeInit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Util u = new Util(c);
                u.saveIcons();
                File f = new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//");



                String [] L = f.list();
                for(int i = 0; i < L.length; i++) {
                    Logger.l(L[i]);
                    //u.uploadImage("", new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//"+L[i]));
                }
                return null;
            }
        }.execute();
        startActivity(new Intent(MainActivity.this, ParentActivity.class));
        this.finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.activityResult(requestCode);
    }


    public void firstTimeInit() throws JSONException {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = this.getPackageManager().queryIntentActivities( mainIntent, 0);
        JSONObject O = new JSONObject();
        JSONArray a = new JSONArray();
        //O.put("array", )


        for(ResolveInfo x : pkgAppsList) {
            ApplicationInfo ai;

            try {
                ai = this.getPackageManager().getApplicationInfo( x.activityInfo.packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
             String appLabel = (String) (ai != null ? this.getPackageManager().getApplicationLabel(ai) : "(unknown)");
            appLabel = appLabel.replaceAll("&", "");
            JSONObject e = new JSONObject();
            Logger.l(appLabel + "   dovrde");

            e.put("name", appLabel);
            e.put("package", x.activityInfo.packageName);
            a.put(e);
        }
        O.put("apps", a);
        O.put("imei", new Device(this).getImei());
        new ServerHelper2(this).execute("https://lookin24.com/initApp", O.toString());


    }


}
