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
        assertNodeIsArrayNodeAndHasIndex(index);
        focusedNode = focusedNode.get(index);
        return this;
    }

    public JsonEditor child(String propertyName) {
        assertNodeIsObjectNodeAndHasProperty(propertyName);
        focusedNode = focusedNode.get(propertyName);
        return this;
    }

    public JsonEditor set(String propertyName, JsonEditor newValueFromCurrentPositionOfEditor) {
        return setValueOfObjectProperty(propertyName, parseJson(newValueFromCurrentPositionOfEditor.focusedNode.toString()));
    }

    public JsonEditor set(String propertyName, int newValue) {
        return setValueOfObjectProperty(propertyName, new IntNode(newValue));
    }

    public JsonEditor set(String propertyName, long newValue) {
        return setValueOfObjectProperty(propertyName, new LongNode(newValue));
    }

    public JsonEditor set(String propertyName, double newValue) {
        return setValueOfObjectProperty(propertyName, new DoubleNode(newValue));
    }

    public JsonEditor set(String propertyName, boolean newValue) {
        return setValueOfObjectProperty(propertyName, booleanNodeForValue(newValue));
    }

    public JsonEditor set(String propertyName, String newValue) {
        return setValueOfObjectProperty(propertyName, new TextNode(newValue));
    }

    public JsonEditor put(String propertyName, JsonEditor newValueFromCurrentPositionOfEditor) {
        return putValueOfObjectProperty(propertyName, parseJson(newValueFromCurrentPositionOfEditor.focusedNode.toString()));
    }

    public JsonEditor put(String propertyName, int newValue) {
        return putValueOfObjectProperty(propertyName, new IntNode(newValue));
    }

    public JsonEditor put(String propertyName, long newValue) {
        return putValueOfObjectProperty(propertyName, new LongNode(newValue));
    }

    public JsonEditor put(String propertyName, double newValue) {
        return putValueOfObjectProperty(propertyName, new DoubleNode(newValue));
    }

    public JsonEditor put(String propertyName, boolean newValue) {
        return putValueOfObjectProperty(propertyName, booleanNodeForValue(newValue));
    }

    public JsonEditor put(String propertyName, String newValue) {
        return putValueOfObjectProperty(propertyName, new TextNode(newValue));
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
        return setAtArrayIndex(index, booleanNodeForValue(newValue));
    }

    public JsonEditor set(int index, String newValue) {
        return setAtArrayIndex(index, new TextNode(newValue));
    }

    public JsonEditor remove(int index) {
        assertNodeIsArrayNodeAndHasIndex(index);
        ((ArrayNode) focusedNode).remove(index);
        return this;
    }

    public JsonEditor remove(String propertyName) {
        ((ObjectNode) focusedNode).remove(propertyName);
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
    private void assertNodeIsArrayNodeAndHasIndex(int index) {
        if (!focusedNode.isArray()) {
            throw new NotAnArrayNodeException();
        }
        if (!focusedNode.has(index)) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private void assertNodeIsObjectNode() {
        if (!focusedNode.isObject()) {
            throw new NotAnObjectNodeException();
        }
    }

    private void assertObjectNodeHasProperty(String propertyName) {
        if (!focusedNode.has(propertyName)) {
            throw new NoSuchPropertyException();
        }
    }

    private void assertNodeIsObjectNodeAndHasProperty(String propertyName) {
        assertNodeIsObjectNode();
        assertObjectNodeHasProperty(propertyName);
    }

    private JsonEditor setAtArrayIndex(int index, JsonNode newNode) {
        assertNodeIsArrayNodeAndHasIndex(index);
        ((ArrayNode) focusedNode).set(index, newNode);
        return this;
    }

    private JsonEditor putValueOfObjectProperty(String propertyName, JsonNode newNode) {
        assertNodeIsObjectNode();
        putPropertyOnObjectNode(propertyName, newNode);
        return this;
    }

    private JsonEditor setValueOfObjectProperty(String propertyName, JsonNode newNode) {
        assertNodeIsObjectNodeAndHasProperty(propertyName);
        putPropertyOnObjectNode(propertyName, newNode);
        return this;
    }

    private void putPropertyOnObjectNode(String propertyName, JsonNode newNode) {
        ((ObjectNode) focusedNode).put(propertyName, newNode);
    }

    private BooleanNode booleanNodeForValue(boolean newValue) {
        return newValue ? BooleanNode.TRUE : BooleanNode.FALSE;
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
