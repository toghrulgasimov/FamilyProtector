package com.family.familyprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ParentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        //getSupportActionBar().hide(); //hide the title bar

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient() { @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        } });

        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]");

        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //myWebView.setRenderPriority(WebSettings.RenderPriority.HIGH);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //http://lookin24.com/
        myWebView.loadUrl("https://lookin24.com/index3.html");
    }
}
