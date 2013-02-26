package com.example.android.sampleapp;

import android.widget.TextView;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;


@RunWith(RobolectricTestRunner.class)
public class StarterActivityTestWithGreatExpectations {
    @Test
//todo
    @Ignore("re-implement with Fest")
    public void shouldHaveATitle() throws FileNotFoundException {
        final StarterActivity activity = new StarterActivity();
        activity.onCreate(null);
        TextView title = (TextView) activity.findViewById(R.id.title);
//        expect(title).not.toBeNull();
//        expect(title.getText()).toBeInstanceOf(String.class);
//        expect(title.getText()).toEqual("Hello World");

        // custom matcher!
//        expect(title).toBeVisible();
    }
}
