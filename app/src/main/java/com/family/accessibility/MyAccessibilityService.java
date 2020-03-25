package com.family.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;


public class MyAccessibilityService extends AccessibilityService {


    //read screen text;
    //https://stackoverflow.com/questions/30909926/get-text-content-of-the-android-screen

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("salam","Accesibilty Service cagrildi");

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d("salam", "EVENT cagrildi");
    }

    @Override
    public void onInterrupt() {

    }
}
