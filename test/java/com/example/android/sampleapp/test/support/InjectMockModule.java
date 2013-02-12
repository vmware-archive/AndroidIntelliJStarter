package com.example.android.sampleapp.test.support;

import com.google.inject.AbstractModule;

import java.util.HashMap;
import java.util.Map;

public class InjectMockModule extends AbstractModule {
    Map<Class, Object> bindingMap = new HashMap<Class, Object>();
    @Override
    protected void configure() {
        for (Class mockClass : bindingMap.keySet()) {
            bind(mockClass).toInstance(bindingMap.get(mockClass));
        }
    }

    public <T> void addBindingForMock(Class<T> mockClass, T mock) {
        bindingMap.put(mockClass, mock);
    }
}
