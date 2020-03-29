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
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.family.familyprotector.FileR;
import com.family.familyprotector.Logger;
import com.family.internet.ServerHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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

    public String bitMaptoString(Drawable drawable) throws IOException {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                bitmap= bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void sendJsonString(String url, JSONObject data) {
        Logger.l("send JsOn String");
        new ServerHelper(this.context).execute(url, data.toString());
    }

    public void sendIconToServer(String packageName) throws PackageManager.NameNotFoundException, IOException, JSONException {
        Drawable icon = context.getPackageManager().getApplicationIcon(packageName);

        ApplicationInfo applicationInfo =
                context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        Resources res = context.getPackageManager().getResourcesForApplication(applicationInfo);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
             icon = res.getDrawableForDensity(applicationInfo.icon,
                    DisplayMetrics.DENSITY_LOW,
                    null);
        }


        String ans = this.bitMaptoString(icon);
        Logger.l(ans);
        JSONObject postData = new JSONObject();
        postData.put("icon", ans);
        postData.put("packageName", packageName);
        sendJsonString("http://tmhgame.tk/uploadIcon", postData);
    }
    public void uploadIconsToServer() {
        final PackageManager pm = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities( mainIntent, 0);
        //pkgAppsList.get(0).

        //List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ResolveInfo packageInfo : pkgAppsList) {
            try {
                Logger.l("Installed package" + packageInfo.activityInfo.packageName);
                sendIconToServer(packageInfo.activityInfo.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
