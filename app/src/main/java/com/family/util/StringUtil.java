package com.family.util;

import android.view.accessibility.AccessibilityNodeInfo;

import com.family.familyprotector.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class StringUtil {
    public String getCharacters(String s) {
        String ans = "";
        char [] c = new char[s.length()];
        for(int i = 0; i < s.length(); i++) {
            if(Character.isLetter(s.charAt(i)) || (s.charAt(i)<='9' && s.charAt(i)>='0')) {
                c[i] = s.charAt(i);

            }else {

                c[i] = ' ';
            }

        }
        ans = new String(c);

        return ans;
    }

    public static String[] aylar = {
            "JANUARY",
            "FEBRUARY",
            "MARCH",
            "APRIL",
            "MAY",
            "JUNE",
            "JULY",
            "AUGUST",
            "SEPTEMBER",
            "OCTOBER",
            "NOVEMBER",
            "DECEMBER"
    };
    public static Set<String> S = new HashSet<>(Arrays.asList(aylar));
    public static Map<String, Integer> M = new HashMap<>();
     static {
        for(int i = 0; i < aylar.length; i++) {
            M.put(aylar[i], i+1);
        }
    }

    public static int[] timeToInt(String s) {
         String[] a = s.split(":");
         int[] ans = {Integer.parseInt(a[0]),Integer.parseInt(a[1])};
         return ans;
    }
    public static void setDateTime(Date d, String s) {
         int[] t = timeToInt(s);
         d.setHours(t[0]);
         d.setMinutes(t[1]);
         d.setSeconds(0);
    }

    public static Date getDate(String s) {
         String[] a = s.split(" ");
         if(a.length != 0) {
             if(a[0].equals("YESTERDAY")) {
                 long cur = System.currentTimeMillis() - 24*60*60*1000;
                 Date d = new Date(cur);
                 return d;
             }else if(a[0].equals("TUDAY")) {
                 long cur = System.currentTimeMillis();
                 Date d = new Date(cur);
                 return d;
             }else if(a.length == 3) {
                 a[1] = a[1].substring(0,3);
                 s = a[2]+"-"+a[1] + "-" + a[0];

                 DateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
                 Date date = null;
                 try {
                     date=df.parse(s);

                 } catch (ParseException e) {
                     e.printStackTrace();
                     return null;
                 }
                 return date;
             }
         }else {

         }
         return new Date();
    }
    public static boolean isDigist(char a) {
        return '0'<=a && a<='9';
    }
    public static boolean isTime(String s) {
        boolean ans = false;
        ans = s.length() == 5 && s.charAt(2) == ':' && isDigist(s.charAt(0))&& isDigist(s.charAt(1))
                && isDigist(s.charAt(3))&& isDigist(s.charAt(4));
        return ans;
    }
    public static boolean onlyUppercase(String s) {
         if(s == null || s.length() == 1) {
             return false;
         }
         String[] a = s.split(" ");
         if(a.length == 1 && (a[0].equals("YESTERDAY") || a[0].equals("TODAY"))) {
             return true;
         }else if(a.length == 3 && S.contains(a[1])) {
             return true;
         }

        return false;
    }
    public static String findTime(AccessibilityNodeInfo n) {
         if(n == null) {
             return null;
         }
         int c = n.getChildCount();
         for(int i = 0; i < c; i++) {
             if(n.getChild(i) == null)
                 continue;
             String txt = n.getChild(i).getText() != null ? n.getChild(i).getText().toString() : null;
             if(txt == null) continue;
             if(isTime(txt)) {
                 return txt;
             }
         }
         return null;
    }

}
