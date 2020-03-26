package com.family.internet;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerHelper extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String s = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL("http://www.tmhgame.tk/ailep");

            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes("PostData=" + params[1]);
            wr.flush();
            wr.close();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                System.out.print(current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return s;
    }
}
