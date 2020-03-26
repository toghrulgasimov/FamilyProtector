package com.family.util;

public class Util {
    public static String milliToTime(long m) {
        m /= 1000;
        long san = m % 60;
        m /= 60;
        long deq = m % 60;
        m /= 60;
        long saat = m % 60;
        return saat + "SAAT" + deq+"DEQ" + san + "SAN";
    }
}
