package com.example.android.sampleapp.test.support;

import com.pivotallabs.greatexpectations.matchers.*;
import static com.pivotallabs.greatexpectations.GreatExpectations.wrapped;

public class Expect {
    public static <T extends Object, M extends ObjectMatcher<T, M>> ObjectMatcher<T, ?> expect(T actual) {
        return wrapped(ObjectMatcher.class, actual);
    }
    public static BooleanMatcher<Boolean, ?> expect(boolean actual) {
        return wrapped(BooleanMatcher.class, actual);
    }
    public static <T extends Boolean, M extends BooleanMatcher<T, M>> BooleanMatcher<T, ?> expect(T actual) {
        return wrapped(BooleanMatcher.class, actual);
    }
    public static <T extends Comparable, M extends ComparableMatcher<T, M>> ComparableMatcher<T, ?> expect(T actual) {
        return wrapped(ComparableMatcher.class, actual);
    }
    public static <T extends java.util.Date, M extends DateMatcher<T, M>> DateMatcher<T, ?> expect(T actual) {
        return wrapped(DateMatcher.class, actual);
    }
    public static <T extends Iterable<X>, X, M extends IterableMatcher<T, X, M>> IterableMatcher<T, X, ?> expect(T actual) {
        return wrapped(IterableMatcher.class, actual);
    }
    public static <T extends String, M extends StringMatcher<T, M>> StringMatcher<T, ?> expect(T actual) {
        return wrapped(StringMatcher.class, actual);
    }
    public static <T extends android.view.View, M extends com.example.android.sampleapp.test.support.matchers.android.ViewMatcher<T, M>> com.example.android.sampleapp.test.support.matchers.android.ViewMatcher<T, ?> expect(T actual) {
        return wrapped(com.example.android.sampleapp.test.support.matchers.android.ViewMatcher.class, actual);
    }
}
