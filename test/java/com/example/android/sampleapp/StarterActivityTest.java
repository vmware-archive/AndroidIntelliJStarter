package com.example.android.sampleapp;

import android.widget.TextView;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
//@RunWith(Enclosed.class)
public class StarterActivityTest {

    @Before
    public void setup() {
        System.out.println("Base Before.");
    }

    @Test
    public void shouldHaveATitle() {
        System.out.println("Base test case.");
        final StarterActivity activity = new StarterActivity();
        activity.onCreate(null);
        TextView title = (TextView) activity.findViewById(R.id.title);
        assertEquals("Hello World", title.getText());
    }

    public static class NestedTestClass {

        @Before
        public void setup() {
            System.out.println("Inner Before.");
        }

        @Test
        public void shouldRunNestedTestCase() throws Exception {
            System.out.println("Nested test case.");
            assertTrue(true);
        }
    }
}
