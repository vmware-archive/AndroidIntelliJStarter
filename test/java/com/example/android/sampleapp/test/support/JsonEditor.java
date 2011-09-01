package com.example.android.sampleapp.test.support;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JsonEditor {

    private JsonNode rootNode;
    private JsonNode focusedNode;

    public JsonEditor(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            rootNode = mapper.readValue(jsonString, JsonNode.class);
            focusedNode = rootNode;
        } catch (IOException e) {
            throw new JsonEditorException(e);
        }
    }

    public JsonEditor get(int index) {
        if (!focusedNode.isArray()) {
            throw new NotAnArrayNodeException();
        }
        if (!focusedNode.has(index)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        focusedNode = focusedNode.get(index);
        return this;
    }

    public JsonEditor get(String propertyName) {
        if (!focusedNode.isObject()) {
            throw new NotAnObjectNodeException();
        }
        if (!focusedNode.has(propertyName)) {
            throw new NoSuchPropertyException();
        }
        focusedNode = focusedNode.get(propertyName);
        return this;
    }

    public Number valueAsNumber() {
        if (focusedNode.isNumber()) {
            return focusedNode.getNumberValue();
        } else {
            throw new NotANumericNodeException();
        }
    }

    public boolean valueAsBoolean() {
        if (focusedNode.isBoolean()) {
            return focusedNode.getBooleanValue();
        } else {
            throw new NotABooleanNodeException();
        }
    }

    public String valueAsString() {
        if (focusedNode.isTextual()) {
            return focusedNode.getTextValue();
        } else {
            throw new NotAStringNodeException();
        }
    }

    public boolean isNullValue() {
        return focusedNode.isNull();
    }

    public static class JsonEditorException extends RuntimeException {
        public JsonEditorException() {
        }

        public JsonEditorException(Throwable throwable) {
            super(throwable);
        }
    }
    public static class NotABooleanNodeException extends JsonEditorException {}
    public static class NotANumericNodeException extends JsonEditorException {}
    public static class NotAStringNodeException extends JsonEditorException {}
    public static class NotAnArrayNodeException extends JsonEditorException {}
    public static class NotAnObjectNodeException extends JsonEditorException {}
    public static class NoSuchPropertyException extends JsonEditorException {}
}
