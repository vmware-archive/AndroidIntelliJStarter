package com.example.android.sampleapp;

import android.widget.TextView;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;

import static com.example.android.sampleapp.test.support.Expect.expect;

@RunWith(RobolectricTestRunner.class)
public class StarterActivityTestWithGreatExpectations {
    @Test
    public void shouldHaveATitle() throws FileNotFoundException {
        final StarterActivity activity = new StarterActivity();
        activity.onCreate(null);
        TextView title = (TextView) activity.findViewById(R.id.title);
        expect(title).not.toBeNull();
        expect(title.getText()).toBeInstanceOf(String.class);
        expect(title.getText()).toEqual("Hello World");

        // custom matcher!
        expect(title).toBeVisible();
    }
}
