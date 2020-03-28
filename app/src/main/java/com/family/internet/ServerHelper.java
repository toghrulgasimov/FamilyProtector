package com.family.internet;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.family.familyprotector.Logger;
import com.family.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerHelper extends AsyncTask<String, Void, String> {

    public Context c;
    public ServerHelper(Context c) {
        this.c = c;
    }
    @Override
    protected String doInBackground(String... params) {
        String s = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(params[0]);

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
            StringBuilder sb = new StringBuilder();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                sb.append(current);
            }
            String ans = sb.toString();

            Logger.l(ans);
            if(params[0].endsWith("initApp")) {
                JSONObject jo = new JSONObject(ans);
                JSONArray a = jo.getJSONArray("apps");
                for(int i = 0; i < a.length(); i++) {
                    Logger.l("ARARAR-" + a.getString(i));
                    new Util(this.c).sendIconToServer(a.getString(i));

                }
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
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }


}
