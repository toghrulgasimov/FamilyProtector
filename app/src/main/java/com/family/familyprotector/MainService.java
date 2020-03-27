package com.family.familyprotector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.l("Main Service started");

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
