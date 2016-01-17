package com.malalaoshi.android;

import com.malalaoshi.android.util.MiscUtil;

import junit.framework.TestCase;

/**
 * Test misc utils class
 * Created by tianwei on 1/17/16.
 */
public class MiscUtilTest extends TestCase {

    public void testPhoneNumber() {
        assertEquals(MiscUtil.isMobilePhone("13800138000"), true);
        assertEquals(MiscUtil.isMobilePhone("1380013800"), false);
        assertEquals(MiscUtil.isMobilePhone("13800 13800"), false);
        assertEquals(MiscUtil.isMobilePhone("138001380000"), false);
        assertEquals(MiscUtil.isMobilePhone("14800138000"), false);
        assertEquals(MiscUtil.isMobilePhone("15800138000"), true);
        assertEquals(MiscUtil.isMobilePhone("17800138000"), true);
        assertEquals(MiscUtil.isMobilePhone("18800138000"), true);
        assertEquals(MiscUtil.isMobilePhone("01062258801"), false);
    }
}
