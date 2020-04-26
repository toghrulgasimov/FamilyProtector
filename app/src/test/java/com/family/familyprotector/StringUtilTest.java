package com.family.familyprotector;

import com.family.util.DoublyLinkedList;
import com.family.util.StringUtil;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.family.util.StringUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @Test
    public void simpleTest() {

//        assertTrue(isTime("12:13"));
//        assertTrue(onlyUppercase("12 APRIL 2020"));
//
//        Date d = new Date(System.currentTimeMillis());
//        System.out.println(d);
//        setDateTime(d, "12:13");
//        System.out.println(d.getSeconds());
//
//

//        DoublyLinkedList<String> L = new DoublyLinkedList<>();
//
//        L.addLast("salam");
//        L.addLast("sagol");
//        L.addFirst("aaa");
//        L.addFirst("bb");
//
//        for(DoublyLinkedList.Node x = L.head; x != null; x = x.next) {
//            System.out.println(x.element);
//        }
        long l = -1;
        boolean p;
        p = l == -1L;
        System.out.println((p ? "true" : "false"));
        Date d = new Date();
        System.out.println(d);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DATE, -1);
        d = cal.getTime();

        System.out.println(d);

        Date b = (Date) d.clone();
        b.setHours(12);
        System.out.println(d);
        System.out.println(b);
    }
}
