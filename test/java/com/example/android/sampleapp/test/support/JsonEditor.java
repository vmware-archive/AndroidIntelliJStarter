package com.example.android.sampleapp.test.support;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.DoubleNode;
import org.codehaus.jackson.node.IntNode;
import org.codehaus.jackson.node.LongNode;
import org.codehaus.jackson.node.TextNode;

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

    public JsonEditor root() {
        focusedNode = rootNode;
        return this;
    }

    public JsonEditor get(int index) {
        assertArrayHasIndex(index);
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

    public JsonEditor set(int index, int newValue) {
        setAtArrayIndex(index, new IntNode(newValue));
        return this;
    }

    public JsonEditor set(int index, long newValue) {
        setAtArrayIndex(index, new LongNode(newValue));
        return this;
    }

    public JsonEditor set(int index, double newValue) {
        setAtArrayIndex(index, new DoubleNode(newValue));
        return this;
    }

    public JsonEditor set(int index, boolean newValue) {
        setAtArrayIndex(index, newValue ? BooleanNode.TRUE : BooleanNode.FALSE);
        return this;
    }

    public JsonEditor set(int index, String newValue) {
        setAtArrayIndex(index, new TextNode(newValue));
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

    private void setAtArrayIndex(int index, JsonNode newNode) {
        assertArrayHasIndex(index);
        ((ArrayNode) focusedNode).set(index, newNode);
    }

    private void assertArrayHasIndex(int index) {
        if (!focusedNode.isArray()) {
            throw new NotAnArrayNodeException();
        }
        if (!focusedNode.has(index)) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
}
