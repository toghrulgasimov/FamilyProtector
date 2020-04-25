package com.family.internet;

import com.family.familyprotector.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetHelper {
    public String send(String link, String data) {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(link);
            Logger.l("POST Cagrilib");
            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(7000);
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            wr.write(data);
            wr.flush();
            wr.close();
            urlConnection.getResponseCode();
            Logger.l("POST getdi");
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);
            int dataa = isw.read();
            StringBuilder sb = new StringBuilder();
            while (dataa != -1) {
                char current = (char) dataa;
                dataa = isw.read();
                sb.append(current);
            }
            String ans = sb.toString();
            Logger.l("POST cavab" + ans);
            return ans;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return "";
    }
}
