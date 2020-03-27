package com.family.familyprotector;

import android.util.Log;

public class Logger {
    public static String TAG = "salam";
    public static void l(String... a) {
        if(a.length == 1) {
            Log.d(TAG, a[0]+"");
        }else {
            Log.d(a[0], a[1]+"");
        }
    }
}
