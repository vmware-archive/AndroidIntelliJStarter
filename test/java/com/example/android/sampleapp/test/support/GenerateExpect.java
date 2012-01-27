package com.example.android.sampleapp.test.support;

import com.pivotallabs.greatexpectations.BaseMatcher;
import com.pivotallabs.robolectricgem.expect.RunnableExpectGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*
 * This class helps you author your own Expect.java for your project,
 * in case you want to have your own custom Great Expectations matchers.
 */
public class GenerateExpect extends RunnableExpectGenerator {

    @SuppressWarnings({"unchecked"})
    private static final Class<? extends BaseMatcher>[] CUSTOM_MATCHERS = new Class[]{
            //MyMatcher.class,       // put your custom matcher classes here
            //MyOtherMatcher.class,
    };

    public GenerateExpect() {
        super("com.example.android.sampleapp.test.support"); // put your package name here
    }

    public static void main(String args[]) throws IOException {
        new GenerateExpect().generateCustomExpect();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List<Class<? extends BaseMatcher>> matcherClasses() {
        List<Class<? extends BaseMatcher>> classes = super.matcherClasses();
        classes.addAll(Arrays.asList(CUSTOM_MATCHERS));
        return classes;
    }

    @Override
    protected String getExpectClassFilePrefix() {
        return "test" + File.separator + "java" + File.separator;
    }

}
