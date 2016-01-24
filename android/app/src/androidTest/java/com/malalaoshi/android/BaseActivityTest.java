package com.malalaoshi.android;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Activity test
 * Created by tianwei on 1/17/16.
 */
public class BaseActivityTest extends UITestCase {

    @Override
    protected void onSetUp() {

    }

    @Override
    protected void onTearDown() throws IllegalAccessException {

    }

    /**
     * This function is called by various TestCase implementations, at tearDown() time, in order
     * to scrub out any class variables.  This protects against memory leaks in the case where a
     * test case creates a non-static inner class (thus referencing the test case) and gives it to
     * someone else to hold onto.
     *
     * @param testCaseClass The class of the derived TestCase implementation.
     * @throws IllegalAccessException
     */
    protected void scrubClass(final Class<?> testCaseClass)
            throws IllegalAccessException {
        final Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            final Class<?> fieldClass = field.getDeclaringClass();
            if (testCaseClass.isAssignableFrom(fieldClass) && !field.getType().isPrimitive()
                    && (field.getModifiers() & Modifier.FINAL) == 0) {
                try {
                    field.setAccessible(true);
                    field.set(this, null);
                } catch (Exception e) {
                    android.util.Log.d("TestCase", "Error: Could not nullify field!");
                }

                if (field.get(this) != null) {
                    android.util.Log.d("TestCase", "Error: Could not nullify field!");
                }
            }
        }
    }
}
