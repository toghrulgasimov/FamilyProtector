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


        for(int i = 0; i < 1000; i++) {
//            Toast.makeText(getApplicationContext(),"Service bashladi",
//                    Toast.LENGTH_SHORT).show();
            Log.d("salam", i + "");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        final Service t = this;


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
