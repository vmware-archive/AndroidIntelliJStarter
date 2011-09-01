package com.example.android.sampleapp.test.support;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.DoubleNode;
import org.codehaus.jackson.node.IntNode;
import org.codehaus.jackson.node.LongNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;

import java.io.IOException;

public class JsonEditor {

    private JsonNode rootNode;
    private JsonNode focusedNode;

    public JsonEditor(String jsonString) {
        rootNode = parseJson(jsonString);
        focusedNode = rootNode;
    }

    public JsonEditor root() {
        focusedNode = rootNode;
        return this;
    }

    public String toJson() {
        if (!focusedNode.isContainerNode()) {
            throw new NotAnArrayOrObjectNodeException();
        }
        return focusedNode.toString();
    }

    public JsonEditor child(int index) {
        assertArrayHasIndex(index);
        focusedNode = focusedNode.get(index);
        return this;
    }

    public JsonEditor child(String propertyName) {
        if (!focusedNode.isObject()) {
            throw new NotAnObjectNodeException();
        }
        if (!focusedNode.has(propertyName)) {
            throw new NoSuchPropertyException();
        }
        focusedNode = focusedNode.get(propertyName);
        return this;
    }

    public JsonEditor set(String propertyName, int newValue) {
        if (!focusedNode.isObject()) {
            throw new NotAnObjectNodeException();
        }
        ((ObjectNode) focusedNode).put(propertyName, newValue);
        return this;
    }

    public JsonEditor set(int index, JsonEditor newValueFromCurrentPositionOfEditor) {
        return setAtArrayIndex(index, parseJson(newValueFromCurrentPositionOfEditor.focusedNode.toString()));
    }

    public JsonEditor set(int index, int newValue) {
        return setAtArrayIndex(index, new IntNode(newValue));
    }

    public JsonEditor set(int index, long newValue) {
        return setAtArrayIndex(index, new LongNode(newValue));
    }

    public JsonEditor set(int index, double newValue) {
        return setAtArrayIndex(index, new DoubleNode(newValue));
    }

    public JsonEditor set(int index, boolean newValue) {
        return setAtArrayIndex(index, newValue ? BooleanNode.TRUE : BooleanNode.FALSE);
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
    public static class NotAnArrayOrObjectNodeException extends JsonEditorException {}
    public static class NotAnArrayNodeException extends JsonEditorException {}
    public static class NotAnObjectNodeException extends JsonEditorException {}
    public static class NoSuchPropertyException extends JsonEditorException {}

    private JsonEditor setAtArrayIndex(int index, JsonNode newNode) {
        assertArrayHasIndex(index);
        ((ArrayNode) focusedNode).set(index, newNode);
        return this;
    }

    private void assertArrayHasIndex(int index) {
        if (!focusedNode.isArray()) {
            throw new NotAnArrayNodeException();
        }
        if (!focusedNode.has(index)) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private JsonNode parseJson(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, JsonNode.class);
        } catch (IOException e) {
            throw new JsonEditorException(e);
        }
    }
}
