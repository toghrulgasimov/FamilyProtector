package com.family.background;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.family.familyprotector.MainActivity;
import com.family.location.LocationService;

public class MyService extends Service {
    public MyService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        LocationService L = new LocationService(this);
        //onTaskRemoved(intent);
        Log.d("salam", "SERVICE BASHLADI");


        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
}
