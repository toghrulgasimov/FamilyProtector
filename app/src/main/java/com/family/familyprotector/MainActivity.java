package com.family.familyprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import android.os.Bundle;

import com.family.accessibility.MyAccessibilityService;
import com.family.location.LocationService;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    public String CHANNEL_ID = "NOT";
    LocationService gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("salam", "alalalal");
        // currently not possible
        //String[] proj = new String[] { Browser., Browser.BookmarkColumns.URL };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            return;
//        }
//        LocationService L = new LocationService(this);


        // disable canceling of notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("content title")
                .setContentText("content text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true);
        createNotificationChannel();


        // show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

        permissions();
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

    public void permissions() {
        String[] p = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET,
                Manifest.permission.KILL_BACKGROUND_PROCESSES, Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.SYSTEM_ALERT_WINDOW
        };
//        for(int i = 0; i < p.length; i++) {
//            if (ContextCompat.checkSelfPermission(this,p[i])!= PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{p[i]},
//                        1);
//                Log.d("salam", "not garanted - " + p[i]);
//            }else {
//                Log.d("salam", "garanted - " + p[i]);
//            }
//        }
//        ActivityCompat.requestPermissions(this,
//                p,
//                1);

//        if(!isAccessibilityServiceEnabled(this, MyAccessibilityService.class)) {
//            setAccesibiltyOn();
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 0);
//            }
//        }


        //set UssageS
//        if(!isUssageS()) {
//            Log.d("salam", "Not garanted");
////            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" + getPackageName()));
////            startActivityForResult(intent, 0);
//
//            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//        }else {
//            Log.d("salam", "garanted");
//        }

        setNotificationAccess();

    }



    public void setNotificationAccess() {



        String packageName = this.getPackageName();
        Set<String> enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(this);
        if(enabledPackages.contains(packageName)) {

        }else {
            getApplicationContext().startActivity(new Intent(
                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }



    }
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

    public void setAccesibiltyOn() {
        Intent openSettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        openSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(openSettings);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }else {
            //Log.d("salam", "not garanted - " + p[i]);
//            ActivityCompat.requestPermissions(this,
//                    new String[]{permissions[0]},
//                    1);
        }
    }
}
