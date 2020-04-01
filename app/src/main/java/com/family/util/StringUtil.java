package com.family.util;

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

}
