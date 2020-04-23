package com.family.familyprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class ParentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        //getSupportActionBar().hide(); //hide the title bar

        WebView myWebView = (WebView) findViewById(R.id.webview);
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
        final SS s = new SS();


        myWebView.setWebViewClient(new WebViewClient(){
//            @Override
//            public WebResourceResponse shouldInterceptRequest (final WebView view, String url) {
//
//                if(url.startsWith("https://www.lookin24.com/login?email=")) {
//                    Logger.l("BAXX", "BANGGGGGGGGGGGGGG");
//                    s.s = url.substring(url.indexOf("?")+1);
//                    Logger.l("BAXX", s.s);
//                }
//                if(url.startsWith("https://lookin24.com/parent?")) {
//                    Logger.l("BAXX", "BANGGGGGGGGGGGGGG");
//                    //s.s = url.substring(url.indexOf("?")+1);
//                    url += ("&"+s.s);
//                    Logger.l("BAXX", url);
//                }
//                if(url.startsWith("https://lookin24.com/childname.html")) {
//                    Logger.l("BAXX", "BANGGGGGGGGGGGGGG");
//                    //s.s = url.substring(url.indexOf("?")+1);
//                    url += ("?"+s.s);
//                    Logger.l("BAXX", url);
//                }
//                if(url.startsWith("https://lookin24.com/childName?")) {
//                    Logger.l("BAXX", "BANGGGGGGGGGGGGGG");
//                    //s.s = url.substring(url.indexOf("?")+1);
//                    url += ("&"+s.s);
//                    Logger.l("BAXX", url);
//                }
//                if(url.contains("lookin24") || true) {
//                    String cookieStr = CookieManager.getInstance().getCookie(url);
//                    if(cookieStr != null) {
//                        cookieStr = cookieStr.replaceAll(" ", "");
//                        String[] s = cookieStr.split(";");
//                        for(int i = 0; i < s.length; i++) {
//                            String[] t = s[i].split("=");
//                            //Logger.l("BAXX", t[0] + "-" + t[1]);
//                        }
//                        Logger.l("BAXX", url);
//                        Logger.l("BAXX", cookieStr);
//
//                    }
//                }
//                return super.shouldInterceptRequest(view, url);
//            }
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




        myWebView.loadUrl("https://www.lookin24.com/index3?imei=" + (new Device(this)).getImei());
    }
}
