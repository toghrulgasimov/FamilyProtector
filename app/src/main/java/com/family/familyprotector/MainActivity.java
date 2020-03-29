package com.family.familyprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Browser;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import android.os.Bundle;

import com.family.AppActivity.AppActivityService;
import com.family.accessibility.MyAccessibilityService;
import com.family.adminstrator.Adminstrator;
import com.family.background.GoogleService;
import com.family.background.MyService;
import com.family.internet.ServerHelper;
import com.family.location.LocationService;
import com.family.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {



    public PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        permissionManager = new PermissionManager(this);
        permissionManager.drawAppPermission();

        Logger.l(new Device().getImei(this));
        startMainService();
        final Context c = this;

//        new AsyncTask<String, String, Void>() {
//
//            @Override
//            protected Void doInBackground(String... strings) {
//                File uploadFile1 = new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//"+"579"+"salam.png");
//                new Util(c).uploadImage("http://tmhgame.tk/abram", uploadFile1);
//                Logger.l("FAYLA", "gonderildi");
//                return null;
//            }
//        }.execute("");

        new AsyncTask<Integer, Integer, Void>() {

            @Override
            protected Void doInBackground(Integer... integers) {
                Util u = new Util(c);
                u.saveIcons();
                File f = new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//");
                String [] L = f.list();
                for(int i = 0; i < L.length; i++) {
                    Logger.l(L[i]);
                    u.uploadImage("", new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//"+L[i]));
                }
                return null;
            }
        }.execute();



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
                        Logger.l(token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppActivityService.getStatus(this);
        }

        startActivity(new Intent(MainActivity.this, ParentActivity.class));
        this.finish();
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
        String imei = new Device().getImei(this);
        try {
            postData.put("t", token);
            postData.put("i", imei);
            new ServerHelper(this).execute("http://tmhgame.tk/updateFirebaseToken", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void postJSON() {
        JSONObject postData = new JSONObject();
        Log.d("posted", "posteddd");
        try {
            postData.put("name", "Toghrul");
            postData.put("address", "address.getText().toString()");
            postData.put("manufacturer", "manufacturer.getText().toString()");
            postData.put("location", "location.getText().toString()");
            postData.put("type", "type.getText().toString()");
            postData.put("deviceID", "deviceID.getText().toString()");
        new ServerHelper(this).execute("http://tmhgame.tk/uploadIcon", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        startService(new Intent(getApplicationContext(), GoogleService.class));
        permissionManager.setAccesibiltyOn();
    }
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
            Logger.l(appLabel);

            e.put("name", appLabel);
            e.put("package", x.activityInfo.packageName);
            a.put(e);
        }
        O.put("apps", a);
        O.put("imei", new Device().getImei(this));
        new ServerHelper(this).execute("http://tmhgame.tk/initApp", O.toString());


    }


}
