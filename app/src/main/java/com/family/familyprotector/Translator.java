package com.family.familyprotector;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Translator {
    public static Map<String, String> M = new HashMap<>();
    public static String language = Locale.getDefault().getLanguage();
    public static Set<String> view = new HashSet<>();
    public static String getWord(String s) {
        String ans = M.get(s);
        if(ans == null) ans = "898";
        return ans;
    }
    static {
        M.put("TODAY", "TODAY");
        M.put("BU GÜN", "TODAY");
        M.put("BUGÜN", "TODAY");
        M.put("HEUTE", "TODAY");
        M.put("СЕГОДНЯ", "TODAY");

        M.put("YESTERDAY", "YESTERDAY");
        M.put("DÜNƏN", "YESTERDAY");
        M.put("DÜN", "YESTERDAY");
        M.put("GESTERN", "YESTERDAY");
        M.put("ВЧЕРА", "YESTERDAY");

        //посмотра
        //wiews
        //görüntüleme
        //Aufrufe baxış
        M.put("views", "views");
        M.put("посмотра", "views");
        M.put("görüntüleme", "views");
        M.put("Aufrufe", "views");
        M.put("baxış", "views");

        view.add("views");
        view.add("посмотра");
        view.add("görüntüleme");
        view.add("Aufrufe");
        view.add("baxış");





    }
}
