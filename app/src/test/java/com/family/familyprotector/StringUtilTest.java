package com.family.familyprotector;

import com.family.util.StringUtil;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static com.family.util.StringUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @Test
    public void simpleTest() {

        assertTrue(isTime("12:13"));
        assertTrue(onlyUppercase("12 APRIL 2020"));

        Date d = new Date(System.currentTimeMillis());
        System.out.println(d);
        setDateTime(d, "12:13");
        System.out.println(d.getSeconds());
    }
}
