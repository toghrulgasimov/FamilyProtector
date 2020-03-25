package com.family.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;


public class MyAccessibilityService extends AccessibilityService {


    //read screen text;
    //https://stackoverflow.com/questions/30909926/get-text-content-of-the-android-screen



    //solution
    //https://stackoverflow.com/questions/40503081/onaccessibilityevent-not-called-at-all


    @Override
    public boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        Log.d("salam", "KEYEVENT");
        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d("salam", "KeyUp");
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d("salam", "KeyDown");
            }
            return true;
        } else {
            return super.onKeyEvent(event);
        }
    }
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("salam","Accesibilty Service cagrildi");

    }
    ArrayList<AccessibilityNodeInfo> textViewNodes;
    private void findChildViews(AccessibilityNodeInfo parentView) {
        if (parentView == null || parentView.getClassName() == null ) {
            return;
        }
        int childCount = parentView.getChildCount();
        if (childCount == 0 && (parentView.getClassName().toString().contentEquals("android.widget.TextView"))) {
            textViewNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildViews(parentView.getChild(i));
            }
        }
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d("salam", "EVENT cagrildi");

        int eventType = accessibilityEvent.getEventType();

        switch (eventType) {

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                textViewNodes = new ArrayList<AccessibilityNodeInfo>();

                findChildViews(rootNode);

                for(AccessibilityNodeInfo mNode : textViewNodes){
                    if(mNode.getText()==null){
                        return;
                    }
                    String tv1Text = mNode.getText().toString();
                    Log.d("salam", tv1Text);

                    //do whatever you want with the text content...

                }
                break;

        }
    }

    @Override
    public void onInterrupt() {

    }
}
