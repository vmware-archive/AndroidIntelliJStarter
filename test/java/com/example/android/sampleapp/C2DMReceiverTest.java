package com.example.android.sampleapp;

import android.content.Intent;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class C2DMReceiverTest {
    private C2DMReceiver c2DMReceiver;

    @Before
    public void setup() {
        c2DMReceiver = new C2DMReceiver();
    }

    @Test
    public void testOnMessage() throws Exception {
        c2DMReceiver.onMessage(Robolectric.application, new Intent());
        assertTrue("Implement your own tests or delete this", true);
    }

    @Test
    public void testOnRegistered() throws Exception {
        c2DMReceiver.onRegistered(Robolectric.application, "reg_id");
        assertTrue("Implement your own tests or delete this", true);
    }

    @Test
    public void testOnError() throws Exception {
        c2DMReceiver.onError(Robolectric.application, C2DMReceiver.ERR_ACCOUNT_MISSING);
        assertTrue("Implement your own tests or delete this", true);
    }

    @Test
    public void testOnUnregistered() throws Exception {
        c2DMReceiver.onUnregistered(Robolectric.application);
        assertTrue("Implement your own tests or delete this", true);
    }
}
