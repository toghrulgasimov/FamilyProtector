package com.family.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.family.familyprotector.MainActivity;
import com.family.internet.ServerHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyAccessibilityService extends AccessibilityService {


    //read screen text;
    //https://stackoverflow.com/questions/30909926/get-text-content-of-the-android-screen



    //solution
    //https://stackoverflow.com/questions/40503081/onaccessibilityevent-not-called-at-all



    public static MyAccessibilityService instance;

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        Log.d("salam", "KEYEVENT");
        if(action == KeyEvent.KEYCODE_POWER) {
            Log.d("power", "poewer basildi");
        }
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
        instance = this;
        final AccessibilityServiceInfo info = getServiceInfo();
        info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        setServiceInfo(info);

    }
    @Override
    protected boolean onGesture(int gestureId) {
        Log.d("ggg", "gasture");
        return super.onGesture(gestureId);
    }
    public void sondur() {
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
    }
    ArrayList<AccessibilityNodeInfo> textViewNodes;
    private void findChildViews(AccessibilityNodeInfo parentView) {
        if (parentView == null || parentView.getClassName() == null ) {
            return;
        }
        int childCount = parentView.getChildCount();
        Log.d("sagol", parentView.getClassName().toString());
        if (childCount == 0 && (parentView.getClassName().toString().contentEquals("android.widget.TextView"))) {

            textViewNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildViews(parentView.getChild(i));
            }
        }
    }


    public void postJSON(String s) {
        JSONObject postData = new JSONObject();
        Log.d("posted", "posted");
        try {
            postData.put("name", s);
            new ServerHelper().execute("http://tmhgame.tk/ailep", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        //Log.d("salam", "EVENT cagrildi");

        int eventType = accessibilityEvent.getEventType();

        switch (eventType) {

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                textViewNodes = new ArrayList<AccessibilityNodeInfo>();

                findChildViews(rootNode);
                String oldText = "";
                for(int i = 0; i < textViewNodes.size(); i++) {
                    AccessibilityNodeInfo mNode = textViewNodes.get(i);
                    if(mNode.getText()==null){
                        return;
                    }
                    String tv1Text = mNode.getText().toString();


                    Log.d("accesibility", tv1Text);



                    if((tv1Text.startsWith("This admin app is active") && oldText.equals("FamilyProtector")) || tv1Text.equals("Locationn")) {
                        Log.d("salam", "Exited");
                        performGlobalAction(GLOBAL_ACTION_BACK);
                        //Intent dialogIntent = new Intent(this, MainActivity.class);
                        //dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(dialogIntent);
                    }
                    oldText = tv1Text;

                }

                break;

        }
    }


    @Override
    public void onInterrupt() {

    }

    //IMPORTANT
    //AccessibilityNodeInfo interactedNodeInfo =
    //                accessibilityEvent.getSource();

}
