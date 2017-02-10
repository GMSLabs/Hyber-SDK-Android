package com.hyber;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    private String correctOsType = "android";

    @Test
    public void getRandomUuidTest() throws Exception {
        String actual = Utils.getRandomUuid();
        assertNotNull(actual);
        assertNotSame(Utils.getRandomUuid(), actual);
    }

    @Test
    public void getOsType() throws Exception {
        String actual = Utils.getOsType();
        assertNotNull(actual);
        assertEquals(correctOsType, actual);
    }

}
