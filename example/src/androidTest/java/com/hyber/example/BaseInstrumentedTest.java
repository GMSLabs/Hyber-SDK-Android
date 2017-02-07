package com.hyber.example;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BaseInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        switch (appContext.getPackageName()) {
            case "com.hyber.example.dev":
                assertEquals("com.hyber.example.dev", appContext.getPackageName());
                break;
            case "com.hyber.example.td":
                assertEquals("com.hyber.example.td", appContext.getPackageName());
                break;
            case "com.hyber.example.prod":
                assertEquals("com.hyber.example.prod", appContext.getPackageName());
                break;
            case "com.hyber.example.prodd":
                assertEquals("com.hyber.example.prodd", appContext.getPackageName());
                break;
            default: assertEquals("UNDEFINED", appContext.getPackageName());
        }
    }
}
