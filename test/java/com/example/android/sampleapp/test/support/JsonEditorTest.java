package com.example.android.sampleapp.test.support;

import org.junit.Test;

import static com.pivotallabs.robolectricgem.expect.Expect.expect;


public class JsonEditorTest {

    @Test(expected = JsonEditor.JsonEditorException.class)
    public void constructor_shouldThrowException_whenJsonIsInvalid() throws Exception {
        String invalidJson = "this is not valid json";
        new JsonEditor(invalidJson);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arrayGet_shouldThrowException_whenArrayIsEmpty() throws Exception {
        String emptyArrayJson = "[]";
        new JsonEditor(emptyArrayJson).get(0);
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arrayGet_shouldThrowException_whenItIsUsedOnAnObject() throws Exception {
        String emptyObjectJson = "{}";
        new JsonEditor(emptyObjectJson).get(0);
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arrayGet_shouldThrowException_whenItIsUsedOnABasicDataType() throws Exception {
        String arrayJson = "[1]";
        new JsonEditor(arrayJson).get(0).get(0);
    }

    @Test
    public void arrayGet_shouldGetElementsOfArray() throws Exception {
        String arrayJson = "[1, 2, 3]";
        expect(new JsonEditor(arrayJson).get(0).valueAsNumber()).toEqual(1);
        expect(new JsonEditor(arrayJson).get(2).valueAsNumber()).toEqual(3);
    }

    @Test
    public void arrayGet_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String arrayJson = "[[1, 2, 3], 4, 5]";
        expect(new JsonEditor(arrayJson).get(0).get(0).valueAsNumber()).toEqual(1);
        expect(new JsonEditor(arrayJson).get(0).get(2).valueAsNumber()).toEqual(3);

        String deeperArrayJson = "[[11, 22, [111, 222, [1111, 2222]]], 2, 3]";
        expect(new JsonEditor(deeperArrayJson).get(0).get(2).get(1).valueAsNumber()).toEqual(222);
        expect(new JsonEditor(deeperArrayJson).get(0).get(2).get(2).get(1).valueAsNumber()).toEqual(2222);
    }

    @Test(expected = JsonEditor.NotAnObjectNodeException.class)
    public void objectGet_shouldThrowException_whenNodeIsNotAnObject() throws Exception {
        String emptyArrayJson = "[]";
        new JsonEditor(emptyArrayJson).get("property_name");
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectGet_shouldThrowException_whenPropertyDoesNotExistOnObject() throws Exception {
        String objectJson = "{\"a\": 1}";
        new JsonEditor(objectJson).get("this property does not exist");
    }

    @Test
    public void objectGet_shouldGetValuesOfObjectProperties() throws Exception {
        String objectJson = "{\"a\": 1}";
        expect(new JsonEditor(objectJson).get("a").valueAsNumber()).toEqual(1);
    }

    @Test
    public void objectGet_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String objectJson = "{\"a\": {\"aa\": 42}}";
        expect(new JsonEditor(objectJson).get("a").get("aa").valueAsNumber()).toEqual(42);

        String nestedObjectJson = "{\"a\": 1, \"b\": {\"aa\": 22, \"bb\": {\"aaa\": 555, \"bbb\": 777}}}";
        expect(new JsonEditor(nestedObjectJson).get("b").get("bb").get("aaa").valueAsNumber()).toEqual(555);
    }

    @Test
    public void get_shouldSupportChainedCalls_mixingArrayGetAndObjectGet() throws Exception {
        String arrayJson = "[{\"a\": {\"aa\": [42, 99]}}]";
        expect(new JsonEditor(arrayJson).get(0).get("a").get("aa").get(1).valueAsNumber()).toEqual(99);
    }

    @Test
    public void valueAsNumber_shouldReturnNumberForNumericNodes() throws Exception {
        String arrayJson = "[1.5, 2.1, 3.9]";
        expect(new JsonEditor(arrayJson).get(0).valueAsNumber()).toEqual(1.5);
        expect(new JsonEditor(arrayJson).get(2).valueAsNumber()).toEqual(3.9);
    }

    @Test(expected = JsonEditor.NotANumericNodeException.class)
    public void valueAsNumber_shouldThrowException_whenTheNodeIsNotANumericNode() throws Exception {
        String arrayJson = "[false]";
        new JsonEditor(arrayJson).get(0).valueAsNumber();
    }

    @Test(expected = JsonEditor.NotANumericNodeException.class)
    public void valueAsNumber_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null]";
        new JsonEditor(arrayJson).get(0).valueAsNumber();
    }

    @Test
    public void valueAsBoolean_shouldReturnBooleanForBooleanNodes() throws Exception {
        String arrayJson = "[false, false, true]";
        expect(new JsonEditor(arrayJson).get(0).valueAsBoolean()).toBeFalse();
        expect(new JsonEditor(arrayJson).get(2).valueAsBoolean()).toBeTrue();
    }

    @Test(expected = JsonEditor.NotABooleanNodeException.class)
    public void valueAsBoolean_shouldThrowException_whenTheNodeIsNotABooleanNode() throws Exception {
        String arrayJson = "[1]";
        new JsonEditor(arrayJson).get(0).valueAsBoolean();
    }

    @Test(expected = JsonEditor.NotABooleanNodeException.class)
    public void valueAsBoolean_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null]";
        new JsonEditor(arrayJson).get(0).valueAsBoolean();
    }

    @Test
    public void valueAsString_shouldReturnStringForBooleanNodes() throws Exception {
        String arrayJson = "[\"string one\", \"string two\", \"string three\"]";
        expect(new JsonEditor(arrayJson).get(0).valueAsString()).toEqual("string one");
        expect(new JsonEditor(arrayJson).get(2).valueAsString()).toEqual("string three");
    }

    @Test(expected = JsonEditor.NotAStringNodeException.class)
    public void valueAsString_shouldThrowException_whenTheNodeIsNotAStringNode() throws Exception {
        String arrayJson = "[1]";
        new JsonEditor(arrayJson).get(0).valueAsString();
    }

    @Test(expected = JsonEditor.NotAStringNodeException.class)
    public void valueAsString_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null]";
        new JsonEditor(arrayJson).get(0).valueAsString();
    }

    @Test
    public void isNullValue_should_checkIfTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null, 1]";
        expect(new JsonEditor(arrayJson).get(0).isNullValue()).toBeTrue();
        expect(new JsonEditor(arrayJson).get(1).isNullValue()).toBeFalse();
    }

// TODO: this is what we're aiming for
//        new JsonEditor(arrayContainingNestedObjectsJson).get(0).get("b").set("c", 67).root().setAt(1, "new string").toJson();
//
//        editor = new JsonEditor(arrayContainingNestedObjectsJson).get(0).get("b").set("c", 67);
//        editor.setAt(1, "new string");
//        editor.toJson();

}
