package com.example.android.sampleapp.test.support.matchers.android;

import android.view.View;
import com.pivotallabs.greatexpectations.MatcherOf;
import com.pivotallabs.greatexpectations.matchers.ObjectMatcher;


/**
 * See https://github.com/xian/great-expectations
 *
 * See classes such as com.pivotallabs.greatexpectations.matchers.ObjectMatcher for
 * this signature
 *
 * T extends [The class annotated in @MatcherOf]
 * M extends [This matcher class]
 *
 * Be sure to add your custom Matcher classes to RunnableExpectGenerator.matcherClasses()
 * and regenerate Expect.java
 */

@MatcherOf(View.class)
public class ViewMatcher<T extends View, M extends ViewMatcher<T, M>> extends ObjectMatcher<T, M> {

    public boolean toBeVisible() {
        return actual.getVisibility() == View.VISIBLE;
    }
}
