package com.family.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.family.familyprotector.FileR;
import com.family.familyprotector.Logger;
import com.family.familyprotector.MainActivity;
import com.family.internet.ServerHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class MyAccessibilityService extends AccessibilityService {




    /*
    Youtube baxilan vidyo
    The First Love (ilk aşk - Azerbaijan tar) Ramiz Guliyev -- android.widget.TextView
2020-04-01 09:17:08.498 8370-8370/com.family.familyprotector D/INFOOO: 611K baxış -- android.widget.TextView
2020-04-01 09:17:08.499 8370-8370/com.family.familyprotector D/INFOOO: Paylaşın -- android.widget.TextView
2020-04-01 09:17:08.499 8370-8370/com.family.familyprotector D/INFOOO: Burada saxlayın: -- android.widget.TextView
2020-04-01 09:17:08.499 8370-8370/com.family.familyprotector D/INFOOO: Abe -- android.widget.TextView
2020-04-01 09:17:08.499 8370-8370/com.family.familyprotector D/INFOOO: 828 abunəçi -- android.widget.TextView
2020-04-01 09:17:08.499 8370-8370/com.family.familyprotector D/INFOOO: ABUNƏ OL -- android.widget.TextView

Youtube search link
https://www.youtube.com/results?search_query=the+show+must+go+on
     */
    //read screen text;
    //https://stackoverflow.com/questions/30909926/get-text-content-of-the-android-screen



    //solution
    //https://stackoverflow.com/questions/40503081/onaccessibilityevent-not-called-at-all



    public static MyAccessibilityService instance;
    public static Set<String> blockedApps;
    public static boolean writeBlockedApp = true;

    //every day havo to reneuw
    public static ArrayList< Ac > activities;
    public static Ac cur = null;

    public class Ac {
        public String pa;
        public long start;
        public long end;
    }

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



    public void buildBlockedApps() {
        blockedApps = new HashSet<String>();
        String ans = "";
        try {
            ans = new FileR().read("blockedapps.txt");
            String[] ar = ans.split("\\|");
            for(int i = 0; i < ar.length; i++) {
                blockedApps.add(ar[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeBlockedApps() {
        if(blockedApps.size() == 0)return;
        if(writeBlockedApp) {

            StringBuilder sb = new StringBuilder("");
            List<String>L = new ArrayList<>(blockedApps);
            sb.append(L.get(0));
            for(int i = 1; i < L.size(); i++) {
                sb.append("|"+L.get(i));
            }
            try {
                new FileR().write("blockedapps.txt", sb.toString(), false);
                //writeBlockedApp = false;
                Logger.l(sb.toString() + " Yazildi uzerine");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        activities = new ArrayList<>();
        buildBlockedApps();

        final Handler h = new Handler();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        writeBlockedApps();
                    }
                });
            }
        }, 0, 1000*60 * 10);


    }
    @Override
    protected boolean onGesture(int gestureId) {
        Log.d("ggg", "gasture");
        return super.onGesture(gestureId);
    }
    public void sondur() {
        performGlobalAction(GLOBAL_ACTION_HOME);
    }
    ArrayList<AccessibilityNodeInfo> textViewNodes;
    private void findChildViews(AccessibilityNodeInfo parentView) {
        if (parentView == null || parentView.getClassName() == null ) {
            return;
        }
        if((parentView.getClassName().toString().contentEquals("android.widget.EditText") ||
        parentView.getClassName().toString().contentEquals("android.widget.TextView"))) {
            String ans = parentView.getText() != null ? parentView.getText().toString() : "null";
            Logger.l("INFOOO", ans + " -- " + parentView.getClassName());
        }

        int childCount = parentView.getChildCount();
        //Log.d("sagol", parentView.getClassName().toString());
        //TextView
        //EditText
        if (childCount == 0) {
            return;
            //textViewNodes.add(parentView);
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
            new ServerHelper(this).execute("http://tmhgame.tk/ailep", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        //Log.d("salam", "EVENT cagrildi");

        int eventType = accessibilityEvent.getEventType();
        AccessibilityNodeInfo ni = accessibilityEvent.getSource();

        findChildViews(ni);

        Log.i("INFO", "---" + ni);



        switch (eventType) {

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                AccessibilityNodeInfo rootNode = getRootInActiveWindow();




                //rootNode.findAccessibilityNodeInfosByViewId()



                if(rootNode == null)return;
                String pname = rootNode.getPackageName() == null ? "" : rootNode.getPackageName().toString();


                if(activities.size() == 0) {
                    Ac ac = new Ac();
                    ac.start = System.currentTimeMillis();
                    ac.pa = pname;
                    ac.end = -1;
                    activities.add(ac);
                    Logger.l(" acctive -------------"+pname);
                }else if(!activities.get(activities.size()-1).pa.equals(pname)) {
                    activities.get(activities.size()-1).end = System.currentTimeMillis();
                    Ac ac = new Ac();
                    ac.start = System.currentTimeMillis();
                    ac.pa = pname;
                    ac.end = -1;
                    activities.add(ac);
                    Logger.l(" acctive -------------"+pname);
                    for(Ac x : activities) {
                        Logger.l(x.pa + "- " + (x.end-x.start));
                    }
                }

                if(rootNode == null)return;
                if(blockedApps == null) {
                    buildBlockedApps();

                }

                if(blockedApps.contains(rootNode.getPackageName().toString())) {
                    sondur();
                }
                textViewNodes = new ArrayList<AccessibilityNodeInfo>();
                //AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                findChildViews(rootNode);
                String oldText = "";
                for(int i = 0; i < textViewNodes.size(); i++) {
                    AccessibilityNodeInfo mNode = textViewNodes.get(i);
                    if(mNode.getText()==null){
                        return;
                    }
                    String tv1Text = mNode.getText().toString();


                    Log.d("accesibility", tv1Text);



                    if((tv1Text.startsWith("TThis admin app is active") && oldText.equals("FamilyProtector")) || tv1Text.equals("Locationn")) {

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
