package com.example.android.sampleapp.test.support;


import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.util.reflection.FieldSetter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class InjectMockAnnotations {
    public static void initInjectMocks(Class testClass, InjectMockModule injectMockModule, Object testInstance) {
        Field[] fields = testClass.getDeclaredFields();
        for (Field field : fields) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType() == InjectMock.class) {
                    addMockToModule(field, injectMockModule, testInstance);
                }
            }
        }
    }

    private static <T extends Object> void addMockToModule(Field field, InjectMockModule injectMockModule, Object testInstance) {
        Class<T> type = (Class<T>) field.getType();
        T mock = Mockito.mock(type);
        injectMockModule.addBindingForMock(type, mock);
        try {
            new FieldSetter(testInstance, field).set(mock);
        } catch (Exception e) {
            throw new MockitoException("Problems setting field " + field.getName() + " annotated with "
                    + InjectMock.class, e);
        }
    }
}