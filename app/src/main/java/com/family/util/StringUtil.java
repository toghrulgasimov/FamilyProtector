package com.family.util;

import java.util.Arrays;
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
    public String getDate(String s) {
         String[] a = s.split(" ");
         if(a.length == 0) {
             if(a[0].equals("YESTERDAY")) {

             }else if(a[0].equals("TUDAY")) {
                 long cur = System.currentTimeMillis();

             }
         }else {

         }
         return "";
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
        for(int i = 0; i < s.length(); i++) {
            if(!Character.isDigit(s.charAt(i)) &&s.charAt(i)!=' ' && !Character.isUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
