package com.example;

import android.widget.TextView;
import com.example.test.support.FakeCurrentTime;
import com.example.test.support.SampleRoboguiceTestRunner;
import com.example.util.CurrentTime;
import com.google.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(SampleRoboguiceTestRunner.class)
public class StarterActivityWithRoboguiceTest {
    @Inject
    CurrentTime currentTime;

    @Test
    public void testCurrentTimeIsInjected() throws Exception {
        ((FakeCurrentTime) currentTime).setCurrentTime(12345L);
        final StarterActivity activity = new StarterActivity();
        activity.onCreate(null);
        TextView title = (TextView) activity.findViewById(R.id.current_time);
        assertEquals("12345", title.getText());
    }
}
