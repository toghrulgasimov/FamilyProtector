package com.family.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.family.familyprotector.FileR;
import com.family.familyprotector.Logger;
import com.family.familyprotector.MultipartUtility;
import com.family.internet.ServerHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Util {
    public static String milliToTime(long m) {
        m /= 1000;
        long san = m % 60;
        m /= 60;
        long deq = m % 60;
        m /= 60;
        long saat = m % 60;
        return saat + "SAAT" + deq+"DEQ" + san + "SAN";
    }

    public Context context;
    public Util(Context c) {
        this.context = c;
    }








    public void saveIcons() {
        final PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities( mainIntent, 0);
        for (ResolveInfo packageInfo : pkgAppsList) {
            try {
                String pname = packageInfo.activityInfo.packageName;
                Drawable d = null;
                this.context.getPackageManager()
                        .getApplicationIcon(pname);
                d = context.getPackageManager().getApplicationIcon(pname);
                new FileR(context).writeDrawableFile(d, pname);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject uploadImage(String url, File file) {

        try {
            Logger.l("INSTALLAPPP", " Servere Gonder Bashladi");
            OkHttpClient client = new OkHttpClient();
            Logger.l("INSTALLAPPP", " Servere2");
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("aa", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                    .build();
            Logger.l("INSTALLAPPP", " Servere3");

            Request request = new Request.Builder().url("https://lookin24.com/image")
                    .post(requestBody).build();
            Logger.l("INSTALLAPPP", " Servere4");

            Response response = client.newCall(request).execute();
            Logger.l("INSTALLAPPP", " Servere5");
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }


            Logger.l("INSTALLAPPP", " Servere Gonder Bashladi");

            Log.d("response", "uploadImage:"+response.body().string());

            return new JSONObject(response.body().string());

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
