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
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {


    public String CHANNEL_ID = "NOT";

    private GoogleMap mMap;

    int DRAW = 0,
            USSAGE = 1,
            BATTERY = 2,
            NOTIFICATION = 3,
            ACCESIBILITY = 4,
            ADMIN = 5,
            SIMPLE = 6;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            checkFolder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ts = Context.TELEPHONY_SERVICE;
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(ts);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //return;
        }else {
            String imei = mTelephonyMgr.getDeviceId();
            Log.d("salamm",imei + "imei");
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //createNotification();
        //setBattery();

        simplePermissions();
        //drawAppPermission();

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
                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("FIFI", token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppActivityService.getStatus(this);
        }





        //postJSON();


    }

    public void postJSONFirebase(String token) {
        JSONObject postData = new JSONObject();
        Log.d("posted", "posJson from Firebase");
        String ts = Context.TELEPHONY_SERVICE;
        String imei = "";
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(ts);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //return;
        }else {
            imei = mTelephonyMgr.getDeviceId();
            Log.d("salamm",imei + "imei");
        }
        try {
            postData.put("t", token);
            postData.put("i", imei);
            new ServerHelper().execute("http://tmhgame.tk/fbt", postData.toString());
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
        new ServerHelper().execute("http://tmhgame.tk/fbt", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createNotification() {
        // disable canceling of notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("content title")
                .setContentText("content text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                .setAutoCancel(false)
//                .setOngoing(true);
        createNotificationChannel();


        // show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    public void drawAppPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, DRAW);
            }
        }
    }
    public void setUssage() {
        if(!isUssageS()) {
            Log.d("salam", "Not garanted");

            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), USSAGE);
        }else {
            Log.d("salam", "garanted");
        }
    }
    public void simplePermissions() {
        String[] p = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET,
                Manifest.permission.KILL_BACKGROUND_PROCESSES, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS
        };
        ActivityCompat.requestPermissions(this,
                p,
                SIMPLE);
    }
    public void setBattery() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivityForResult(intent, BATTERY);
            }
        }
    }
    public void setNotificationAccess() {



        String packageName = this.getPackageName();
        Set<String> enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(this);
        if(enabledPackages.contains(packageName)) {

        }else {
            startActivityForResult(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), NOTIFICATION);
//            getApplicationContext().startActivity(new Intent(
//                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }
    public void setAccesibiltyOn() {
        if(!isAccessibilityServiceEnabled(this, MyAccessibilityService.class)) {
            Intent openSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivityForResult(openSettings, ACCESIBILITY);
        }

    }
    public void setAdmin() {
        //https://developer.android.com/guide/topics/admin/device-admin
        //https://stackoverflow.com/questions/30130163/enable-device-admin-dialog-not-showing
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(this, Adminstrator.class);

        if(mDPM != null &&mDPM.isAdminActive(mDeviceAdmin)) {
            Log.d("salam", "Active");
        }else {
            Log.d("salam", "Not Active");

            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "EXPLANATION");
            startActivityForResult(intent, ADMIN);
        }
    }

    DevicePolicyManager mDPM;
    ComponentName mDeviceAdmin;


    public boolean isUssageS() {
        AppOpsManager appOps = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            appOps = (AppOpsManager) this
                    .getSystemService(Context.APP_OPS_SERVICE);
        }
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), this.getPackageName());
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        startService(new Intent(getApplicationContext(), GoogleService.class));
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }else {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DRAW) {
            setUssage();
        }else if(requestCode == USSAGE) {
            setBattery();
        }else if(requestCode == BATTERY) {
            setNotificationAccess();
        }else if(requestCode == NOTIFICATION) {
            setAdmin();
        }else if(requestCode == ADMIN) {
            setAccesibiltyOn();
        }else if(requestCode == ACCESIBILITY) {
        }
        Log.d("salam","result came" + requestCode);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
