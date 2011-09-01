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
    public void isNullValue_shouldCheckIfTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null, 1]";
        expect(new JsonEditor(arrayJson).get(0).isNullValue()).toBeTrue();
        expect(new JsonEditor(arrayJson).get(1).isNullValue()).toBeFalse();
    }

    @Test
    public void arraySetBoolean_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        expect(editor.root().get(0).valueAsNumber()).toEqual(1);

        editor.root().set(0, true);
        expect(editor.root().get(0).valueAsBoolean()).toBeTrue();

        editor.root().set(0, false);
        expect(editor.root().get(0).valueAsBoolean()).toBeFalse();
    }

    @Test
    public void arraySetInt_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 3);
        expect(editor.get(0).valueAsNumber()).toEqual(3);
    }

    @Test
    public void arraySetInt_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String arrayJson = "[1, 2, 3]";

        JsonEditor editor = new JsonEditor(arrayJson);
        expect(editor.set(0, 4).get(0).valueAsNumber()).toEqual(4);

        editor = new JsonEditor(arrayJson);
        editor.set(0, 4).set(1, 5);
        expect(editor.get(0).valueAsNumber()).toEqual(4);
        expect(editor.root().get(1).valueAsNumber()).toEqual(5);
    }

    @Test
    public void arraySetDouble_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 3.66);
        expect(editor.get(0).valueAsNumber()).toEqual(3.66);
    }

    @Test
    public void arraySetLong_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 344L);
        expect(editor.get(0).valueAsNumber()).toEqual(344L);
    }

    @Test
    public void arraySetString_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, "new value");
        expect(editor.get(0).valueAsString()).toEqual("new value");
    }

    @Test
    public void arraySetJsonEditor_shouldChangeTheValueAtTheGivenIndexInTheArray_basedOnTheCurrentPositionOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, new JsonEditor("[1, [2, 3]]").get(1));
        expect(editor.get(0).get(1).valueAsNumber()).toEqual(3);
        expect(editor.root().toJson()).toEqual("[[2,3]]");
    }

    @Test
    public void arraySetJsonEditor_shouldNotCrash_whenCreatingAPotentialLoopInTheDom() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        expect(editor.set(0, editor).toJson()).toEqual("[[1]]");
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arraySet_shouldThrowException_whenItIsNotUsedOnAnArray() throws Exception {
        new JsonEditor("{}").set(0, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arraySet_shouldThrowException_whenArrayIsEmpty() throws Exception {
        String emptyArrayJson = "[]";
        new JsonEditor(emptyArrayJson).set(0, 2);
    }

    @Test(expected = JsonEditor.NotAnObjectNodeException.class)
    public void objectSet_shouldThrowException_whenItIsNotUsedOnAnObject() throws Exception {
        new JsonEditor("[]").set("a", 2);
    }

    @Test
    public void objectSetInt_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0}");
        editor.set("a", 3);
        expect(editor.get("a").valueAsNumber()).toEqual(3);
    }

    @Test
    public void objectSetInt_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String objectJson = "{\"a\": 2, \"b\": 3}";

        JsonEditor editor = new JsonEditor(objectJson);
        expect(editor.set("a", 4).get("a").valueAsNumber()).toEqual(4);

        editor = new JsonEditor(objectJson);
        editor.set("a", 4).set("b", 5);
        expect(editor.get("a").valueAsNumber()).toEqual(4);
        expect(editor.root().get("b").valueAsNumber()).toEqual(5);
    }

    @Test
    public void root_shouldReturnTheEditorBackToTheRootOfTheDomTree() throws Exception {
        String json = "[1, {\"a\": 2}]";
        expect(new JsonEditor(json).get(1).get("a").root().get(0).valueAsNumber()).toEqual(1);
    }

    @Test
    public void toJson_shouldConvertTheCurrentNodeOfTheDomToJson_whenThatNodeIsAnArrayOrObject() throws Exception {
        String json = "[1, {\"a\": 2}]";
        expect(new JsonEditor(json).get(1).get("a").root().toJson()).toEqual(json.replace(" ", ""));
        expect(new JsonEditor(json).get(1).toJson()).toEqual("{\"a\":2}");
    }

    @Test(expected = JsonEditor.NotAnArrayOrObjectNodeException.class)
    public void toJson_shouldThrowException_whenTheCurrentNodeIsNotAnArrayOrObject() throws Exception {
        new JsonEditor("[1, {\"a\": 2}]").get(1).get("a").toJson();
    }
}
