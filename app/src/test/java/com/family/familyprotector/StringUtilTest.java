package com.family.familyprotector;

import com.family.util.StringUtil;

import org.junit.Test;

import static com.family.util.StringUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @Test
    public void simpleTest() {

        assertTrue(isTime("12:13"));
        assertTrue(onlyUppercase("12 APRIL 2020"));
    }
}
