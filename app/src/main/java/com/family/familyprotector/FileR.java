package com.family.familyprotector;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FileR {
    Context context;
    public FileR(Context context) {
        this.context = context;
    }
    public String read(String Filename) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "//.FamilyProtector//"+Filename));
        String line;
        StringBuilder ans = new StringBuilder("");
        while ((line = reader.readLine()) != null)
        {
            ans.append(line);
        }
        reader.close();
        return ans.toString();
    }
    public static void checkFolder() throws IOException {
        File mFolder = new File(Environment.getExternalStorageDirectory(), ".FamilyProtector");
        if (!mFolder.exists()) {
            mFolder.mkdirs();
            mFolder.setExecutable(true);
            mFolder.setReadable(true);
            mFolder.setWritable(true);
        }
        File file = new File(Environment.getExternalStorageDirectory() + "//.FamilyProtector//blockedapps.mp3");
        if(!file.exists()) {
            file.createNewFile();
            Log.d("file", "created");
        }else {
            Log.d("file", "exist");
        }
        file = new File(Environment.getExternalStorageDirectory() + "//.FamilyProtector//locations.mp3");
        if(!file.exists()) {
            file.createNewFile();
        }

    }
    public void write(String name, String txt, boolean append) throws IOException {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        fw = new FileWriter(Environment.getExternalStorageDirectory() + "//.FamilyProtector//"+ name, append);
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        pw.print(txt);
        pw.flush();
        pw.close();
        bw.close();
        fw.close();


    }
    public static void append(String name, String txt) {
        try {
            File f = new File(Environment.getExternalStorageDirectory() + "//.FamilyProtector//"+ name);
            FileWriter writer = new FileWriter(f.getAbsoluteFile(), true);
            writer.append(txt);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void writeIcon(String pname) {
        try {
            Drawable d = this.context.getPackageManager().getApplicationIcon(pname);

            writeDrawableFile(d, pname);

        } catch (JSONException|IOException|PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void writeDrawableFile(Drawable drawable, String pname) throws IOException, JSONException {
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        encodedImage = encodedImage.replace("\n", "");
        Logger.l("encoded", encodedImage);

        imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        writeToFile(imageBytes, pname);
    }
    public void writeToFile(byte[] array, String pname) throws IOException {
        try {
            String path = Environment.getExternalStorageDirectory() + "//.FamilyProtector//"+pname+".png";
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(array);
            stream.close();
            Logger.l("-------fayla yazildi");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
