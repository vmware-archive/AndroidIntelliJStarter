package com.example.android.sampleapp;

import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.FileNotFoundException;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(RobolectricTestRunner.class)
public class StarterActivityWithFestTest {
    @Test
    public void shouldHaveATitle() throws FileNotFoundException {
        final StarterActivity activity = new StarterActivity();
        activity.onCreate(null);
        TextView title = (TextView) activity.findViewById(R.id.title);
        assertThat(title).isNotNull()
                .hasText("Hello World")
                .isVisible();
    }
}
