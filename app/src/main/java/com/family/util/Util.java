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

    public Bitmap bitMaptoString(Drawable drawable) throws IOException, JSONException {
        Bitmap bitmap = null;
        Logger.l("----------------------bitMasd0acalled");

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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        encodedImage = encodedImage.replace("\n", "");
        Logger.l("encoded", encodedImage);

        imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        writeToFile(imageBytes);

        return bitmap;
    }
    public void writeToFile(byte[] array) throws IOException {
        try {
            String path = Environment.getExternalStorageDirectory() + "//FamilyProtector//"+((int)(Math.random()*1000))+"salam.png";
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(array);
            stream.close();
            Logger.l("-------fayla yazildi");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void sendJsonString(String url, JSONObject data) {
        Logger.l("sagollar", data.toString());
        new ServerHelper(this.context).execute(url, data.toString());
    }

    public void sendIconToServer(final String packageName) throws PackageManager.NameNotFoundException, IOException, JSONException {
        Drawable icon = context.getPackageManager().getApplicationIcon(packageName);

        ApplicationInfo applicationInfo =
                context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        Resources res = context.getPackageManager().getResourcesForApplication(applicationInfo);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
             icon = res.getDrawableForDensity(applicationInfo.icon,
                    DisplayMetrics.DENSITY_LOW,
                    null);
        }


        final Bitmap ans = this.bitMaptoString(icon);

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

    public static void postData(Bitmap imageToSend, String pname) {
        try
        {
            URL url = new URL("http://tmhgame.tk/abram");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("fname", pname);

            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);

            // directly let .compress write binary image data
            // to the output-stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageToSend.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();


//            OutputStream os = conn.getOutputStream();
//            imageToSend.compress(Bitmap.CompressFormat.JPEG, 100, os);
//            os.flush();
//            os.close();

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes("PostData=");
            wr.write(imageBytes);
            wr.flush();
            wr.close();



            System.out.println("Response Code: " + conn.getResponseCode());

            InputStream in = new BufferedInputStream(conn.getInputStream());
            Log.d("sdfs", "sfsd");
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            Logger.l("SERVER", response);
            System.out.println(response);

            conn.disconnect();
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }


    public void postMultipart() {
        String charset = "UTF-8";
        File uploadFile1 = new File(Environment.getExternalStorageDirectory() + "//FamilyProtector//"+"579"+"salam.png");
        String requestURL = "http://tmhgame.tk/abram";

        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");

            multipart.addFormField("description", "Cool Pictures");
            multipart.addFormField("keywords", "Java,upload,Spring");

            multipart.addFilePart("fileUpload", uploadFile1);

            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public JSONObject uploadImage(String url, File file) {

        try {

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("aa", "simpleumage", RequestBody.create(MediaType.parse("image/png"), file))
                    .build();

            Request request = new Request.Builder().url("http://tmhgame.tk/image")
                    .post(requestBody).build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            Log.d("response", "uploadImage:"+response.body().string());

            return new JSONObject(response.body().string());

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("salam", "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("salam", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }

}
