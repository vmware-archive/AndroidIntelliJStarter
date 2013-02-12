package com.example.android.sampleapp.test.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunnerWithInjection.class)
public class MockitoTest {
    @Mock ArrayList mockList;

    @Test
    public void testMockitoWorks() throws Exception {
        mockList.add("foo");
        verify(mockList).add("foo");
    }
}
