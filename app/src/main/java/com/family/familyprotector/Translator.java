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

    public static Map<String, Set<String>> MS = new HashMap<>();
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
        view.add("посмотра");//просмотра
        view.add("просмотров");
        view.add("просмотра");
        view.add("görüntüleme");
        view.add("Aufrufe");
        view.add("baxış");

        //Use location
        //Location
        MS.put("Installed", new HashSet<String>());
        MS.put("Location", new HashSet<String>());
        MS.put("Running app", new HashSet<String>());
        MS.put("Use location", new HashSet<String>());
        MS.put("Device admin app", new HashSet<String>());
        MS.put("Device administrator", new HashSet<String>());
        MS.put("Location sources", new HashSet<String>());
        MS.put("Lookin24?", new HashSet<String>());

        MS.get("Installed").add("Installed");
        MS.get("Installed").add("Quraşdırıldı");
        MS.get("Installed").add("Yüklendi");
        MS.get("Installed").add("Установлено");
        MS.get("Installed").add("Installiert");
        //Установлено
        //Installiert

        MS.get("Location").add("Location");
        MS.get("Location").add("Yer");
        MS.get("Location").add("Konum");
        MS.get("Location").add("Передача геоданных");
        MS.get("Location").add("Standort");
        //Передача геоданных
        //Standort

        MS.get("Running app").add("Running app");
        MS.get("Running app").add("Tətbiq prosesdədir");
        MS.get("Running app").add("Çalışan uygulamalar");
        MS.get("Running app").add("Работающие приложения");
        MS.get("Running app").add("Aktive App");
        //Работающие приложения
        //Aktive App

        MS.get("Use location").add("Use location");
        MS.get("Use location").add("Məkanı istifadə edin");
        MS.get("Use location").add("Konumu kullan");
        MS.get("Use location").add("Использовать местоположение");
        MS.get("Use location").add("Standort verwenden");
        //Использовать местоположение
        //Standort verwenden

        MS.get("Device administrator").add("Device administrator");
        MS.get("Device administrator").add("Cihaz yöneticisi");
        MS.get("Device administrator").add("Geräteadministrator");
        MS.get("Device administrator").add("Администратор устройства");
        MS.get("Device administrator").add("Cihaz adminstratoru");
        //MS.get("Device administrator").add("Device administrator");
        //App zur Geräteverwaltung
        // Cihaz admin tətbiqi
        //Приложение администратора устройства
        //Cihaz yönetimi uygulaması
        //Device admin app
        MS.get("Device admin app").add("Device admin app");
        MS.get("Device admin app").add("Cihaz yönetimi uygulaması");
        MS.get("Device admin app").add("Приложение администратора устройства");
        MS.get("Device admin app").add("Cihaz admin tətbiqi");
        MS.get("Device admin app").add("App zur Geräteverwaltung");



        MS.get("Location sources").add("Location sources");
        MS.get("Location sources").add("Konum kaynakları");
        MS.get("Location sources").add("Standortquellen");
        MS.get("Location sources").add("Источники данных о местоположении");


        MS.get("Lookin24?").add("Lookin24?");


        //Konum kaynakları
        //Standortquellen
        //Location sources
        //Источники данных о местоположении


    }
}
