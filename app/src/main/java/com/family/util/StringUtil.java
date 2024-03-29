package com.family.util;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.family.familyprotector.Logger;
import com.family.familyprotector.Translator;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    public static Set<String> S;
    public static Map<String, Integer> M = new HashMap<>();
     static {
         calculateMonth();
    }
    public static void calculateMonth() {
        DateFormatSymbols dfs = new DateFormatSymbols();
        M.clear();
        String[] months = dfs.getMonths();
        for(int i = 0; i < months.length; i++) {
            months[i] = months[i].toUpperCase();
            M.put(months[i], i+1);
        }
        S = new HashSet<>(Arrays.asList(months));
    }

    public static int[] timeToInt(String s) {
         if(s.length() > 3 && (s.endsWith("PM") || s.endsWith("AM"))) {
             s = s.substring(0, s.length()-3);
         }
         String[] a = s.split(":");
         int[] ans = {Integer.parseInt(a[0]),Integer.parseInt(a[1])};
         return ans;
    }
    public static Calendar setDateTime2(Calendar d, String s) {
        int[] t = timeToInt(s);

        d.set(Calendar.HOUR_OF_DAY, t[0]);
        d.set(Calendar.MINUTE, t[1]);
        d.set(Calendar.SECOND, 0);

//        d.setHours(t[0]);
//        d.setMinutes(t[1]);
//        d.setSeconds(0);
        return d;
    }

    public static Calendar getDate(String s) {
         String[] a = s.split(" ");
         if(a.length != 0) {
             if(Translator.getWord(a[0]).equals("YESTERDAY")) {
                 //long cur = System.currentTimeMillis() - 24*60*60*1000;
                 Date d;
                 Calendar cal = Calendar.getInstance();
                 cal.set(Calendar.HOUR_OF_DAY, 0);
                 cal.set(Calendar.MINUTE, 0);
                 cal.set(Calendar.SECOND, 0);
                 cal.add(Calendar.DATE, -1);

                 return cal;
             }else if(Translator.getWord(a[0]).equals("TODAY")) {
                 Date d;
                 Calendar cal = Calendar.getInstance();
                 cal.set(Calendar.HOUR_OF_DAY, 0);
                 cal.set(Calendar.MINUTE, 0);
                 cal.set(Calendar.SECOND, 0);
                 return cal;
             }else if(a.length == 3 || a.length  == 4) {
                 a[1] = a[1].substring(0,3);
                 s = a[2]+"-"+a[1] + "-" + a[0];

                 DateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
                 Date date = null;
                 Calendar cal = null;
                 try {
                     date=df.parse(s);
                     cal = Calendar.getInstance();
                     cal.setTime(date);
                     //Logger.l("YYYYY", date.toString() + "---" + s);
                 } catch (ParseException e) {
                     e.printStackTrace();
                     return null;
                 }
                 return cal;
             }
         }else {

         }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
         return cal;
    }
    public static boolean isDigist(char a) {
        return '0'<=a && a<='9';
    }
    public static boolean isTime(String s) {
         Logger.l("ISTIME", s);
        boolean ans = false;
        if(s.endsWith("PM") || s.endsWith("AM"))
            s = s.substring(0, s.length()-3);
        if(s.length() == 4) {
            s = "0" + s;
        }

        ans = s.length() == 5 && s.charAt(2) == ':' && isDigist(s.charAt(0))&& isDigist(s.charAt(1))
                && isDigist(s.charAt(3))&& isDigist(s.charAt(4));
        Logger.l("ISTIME", s + ans);
        return ans;
    }
    public static boolean onlyUppercase(String s) {
         if(s == null || s.length() == 1) {
             return false;
         }
         String[] a = s.split(" ");
         if(a.length == 1 && (Translator.getWord(a[0]).equals("YESTERDAY") || Translator.getWord(a[0]).equals("TODAY"))) {
             return true;
         }else if((a.length == 3 || a.length==4) && S.contains(a[1])) {
             return true;
         }

        return false;
    }
    public static String findTime(AccessibilityNodeInfo n) {
         if(n == null) {
             return null;
         }
         int c = n.getChildCount();
         for(int i = c-1; i >= 0; i--) {
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
    public static int voiceToSecond(String c) {
         if(!c.startsWith("Voice")) {
             return -10;
         }
         String[] s = c.split(" ");
         String s2[] = s[1].split(":");
         int m = Integer.parseInt(s2[0]);
         int se = Integer.parseInt(s2[1]);
         return m * 60 + se;
    }
    public static int parseVersion(String s) {
         try {
             int ans = Integer.parseInt(s.charAt(0) + "");
             return ans;
         }catch (Exception e){
             return 0;
         }
    }
    public static boolean isNowWithoutDay(String s) {
         if(s == null || s.indexOf(":") == -1) {
             return false;
         }
         if(s.length() == 4) {
             s = "0"+s;
         }
         Calendar now = Calendar.getInstance();
         Date nowd = now.getTime();
         String []a = s.split(":");
         int h = Integer.parseInt(a[0]);
         int m = Integer.parseInt(a[1]);
         Logger.l("IZKNOW", s + "-" + h + "-" + m + " .." + now.get(Calendar.HOUR_OF_DAY) + "-" + now.get(Calendar.MINUTE));
         return (now.get(Calendar.HOUR_OF_DAY) == h || now.get(Calendar.HOUR)==h) && now.get(Calendar.MINUTE) == m;
    }
    public static String removePM(String s) {
         if(s == null) return null;
        if((s.endsWith("PM")|| s.endsWith("AM")) && s.length() > 2)
            s = s.substring(0, s.length()-3);
        if(s.length() == 4) {
            s = "0" + s;
        }
        return s;
    }
    public static int dist(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[len1][len2];
    }
    public static boolean isPrefix(String a, String b) {
         int l = Math.min(a.length(), b.length());
         if(l == 0) return false;
         for(int i = 0; i < l; i++) {
             if(a.charAt(i) != b.charAt(i)) return false;
         }
         return true;
    }

}
