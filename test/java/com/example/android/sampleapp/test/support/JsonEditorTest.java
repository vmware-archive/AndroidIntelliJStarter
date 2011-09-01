package com.example.android.sampleapp.test.support;

import org.junit.Test;

import static com.pivotallabs.robolectricgem.expect.Expect.expect;


public class JsonEditorTest {

    @Test
    public void exampleOfUsingThisClassToEditJson() throws Exception {
        String json = "[1, 2, {\"a\": [[7, 8], 3, 4]}, 5, 999]";
        String expectedEditedJson = "[1.5,\"hello\",{\"a\":[[true,8],3,4],\"new_prop\":\"value\"},[\"more_json\"]]";

        // extreme chaining example
        String editedJson = new JsonEditor(json)
                .set(0, 1.5)
                .set(1, "hello")
                .child(2) // move the current position of the editor to the third child
                .set("new_prop", "value")
                .child("a").child(0) // move deeper in the tree
                .set(0, true)
                .root() // move back to the root node
                .set(3, new JsonEditor("[\"more_json\"]"))
                .remove(4)
                .toJson();

        expect(editedJson).toEqual(expectedEditedJson);

        // same example with less chaining
        JsonEditor e = new JsonEditor(json);
        e.root().set(0, 1.5);
        e.root().set(1, "hello");
        e.root().child(2).set("new_prop", "value");
        e.root().child(2).child("a").child(0).set(0, true);
        e.root().set(3, new JsonEditor("[\"more_json\"]"));
        e.root().remove(4);

        expect(e.toJson()).toEqual(expectedEditedJson);
    }

    @Test(expected = JsonEditor.JsonEditorException.class)
    public void constructor_shouldThrowException_whenJsonIsInvalid() throws Exception {
        String invalidJson = "this is not valid json";
        new JsonEditor(invalidJson);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arrayChild_shouldThrowException_whenArrayIsEmpty() throws Exception {
        String emptyArrayJson = "[]";
        new JsonEditor(emptyArrayJson).child(0);
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arrayChild_shouldThrowException_whenItIsUsedOnAnObject() throws Exception {
        String emptyObjectJson = "{}";
        new JsonEditor(emptyObjectJson).child(0);
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arrayChild_shouldThrowException_whenItIsUsedOnABasicDataType() throws Exception {
        String arrayJson = "[1]";
        new JsonEditor(arrayJson).child(0).child(0);
    }

    @Test
    public void arrayChild_shouldMoveTheCurrentNodeToAnElementOfTheArray() throws Exception {
        String arrayJson = "[1, 2, 3]";
        expect(new JsonEditor(arrayJson).child(0).valueAsNumber()).toEqual(1);
        expect(new JsonEditor(arrayJson).child(2).valueAsNumber()).toEqual(3);
    }

    @Test
    public void arrayChild_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String arrayJson = "[[1, 2, 3], 4, 5]";
        expect(new JsonEditor(arrayJson).child(0).child(0).valueAsNumber()).toEqual(1);
        expect(new JsonEditor(arrayJson).child(0).child(2).valueAsNumber()).toEqual(3);

        String deeperArrayJson = "[[11, 22, [111, 222, [1111, 2222]]], 2, 3]";
        expect(new JsonEditor(deeperArrayJson).child(0).child(2).child(1).valueAsNumber()).toEqual(222);
        expect(new JsonEditor(deeperArrayJson).child(0).child(2).child(2).child(1).valueAsNumber()).toEqual(2222);
    }

    @Test(expected = JsonEditor.NotAnObjectNodeException.class)
    public void objectChild_shouldThrowException_whenNodeIsNotAnObject() throws Exception {
        String emptyArrayJson = "[]";
        new JsonEditor(emptyArrayJson).child("property_name");
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectChild_shouldThrowException_whenPropertyDoesNotExistOnObject() throws Exception {
        String objectJson = "{\"a\": 1}";
        new JsonEditor(objectJson).child("this property does not exist");
    }

    @Test
    public void objectChild_shouldMoveTheCurrentNodeToTheValueOfThatPropertyOfTheObject() throws Exception {
        String objectJson = "{\"a\": 1}";
        expect(new JsonEditor(objectJson).child("a").valueAsNumber()).toEqual(1);
    }

    @Test
    public void objectChild_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String objectJson = "{\"a\": {\"aa\": 42}}";
        expect(new JsonEditor(objectJson).child("a").child("aa").valueAsNumber()).toEqual(42);

        String nestedObjectJson = "{\"a\": 1, \"b\": {\"aa\": 22, \"bb\": {\"aaa\": 555, \"bbb\": 777}}}";
        expect(new JsonEditor(nestedObjectJson).child("b").child("bb").child("aaa").valueAsNumber()).toEqual(555);
    }

    @Test
    public void child_shouldSupportChainedCalls_mixingArrayChildAndObjectChild() throws Exception {
        String arrayJson = "[{\"a\": {\"aa\": [42, 99]}}]";
        expect(new JsonEditor(arrayJson).child(0).child("a").child("aa").child(1).valueAsNumber()).toEqual(99);
    }

    @Test
    public void valueAsNumber_shouldReturnNumberForNumericNodes() throws Exception {
        String arrayJson = "[1.5, 2.1, 3.9]";
        expect(new JsonEditor(arrayJson).child(0).valueAsNumber()).toEqual(1.5);
        expect(new JsonEditor(arrayJson).child(2).valueAsNumber()).toEqual(3.9);
    }

    @Test(expected = JsonEditor.NotANumericNodeException.class)
    public void valueAsNumber_shouldThrowException_whenTheNodeIsNotANumericNode() throws Exception {
        String arrayJson = "[false]";
        new JsonEditor(arrayJson).child(0).valueAsNumber();
    }

    @Test(expected = JsonEditor.NotANumericNodeException.class)
    public void valueAsNumber_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null]";
        new JsonEditor(arrayJson).child(0).valueAsNumber();
    }

    @Test
    public void valueAsBoolean_shouldReturnBooleanForBooleanNodes() throws Exception {
        String arrayJson = "[false, false, true]";
        expect(new JsonEditor(arrayJson).child(0).valueAsBoolean()).toBeFalse();
        expect(new JsonEditor(arrayJson).child(2).valueAsBoolean()).toBeTrue();
    }

    @Test(expected = JsonEditor.NotABooleanNodeException.class)
    public void valueAsBoolean_shouldThrowException_whenTheNodeIsNotABooleanNode() throws Exception {
        String arrayJson = "[1]";
        new JsonEditor(arrayJson).child(0).valueAsBoolean();
    }

    @Test(expected = JsonEditor.NotABooleanNodeException.class)
    public void valueAsBoolean_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null]";
        new JsonEditor(arrayJson).child(0).valueAsBoolean();
    }

    @Test
    public void valueAsString_shouldReturnStringForBooleanNodes() throws Exception {
        String arrayJson = "[\"string one\", \"string two\", \"string three\"]";
        expect(new JsonEditor(arrayJson).child(0).valueAsString()).toEqual("string one");
        expect(new JsonEditor(arrayJson).child(2).valueAsString()).toEqual("string three");
    }

    @Test(expected = JsonEditor.NotAStringNodeException.class)
    public void valueAsString_shouldThrowException_whenTheNodeIsNotAStringNode() throws Exception {
        String arrayJson = "[1]";
        new JsonEditor(arrayJson).child(0).valueAsString();
    }

    @Test(expected = JsonEditor.NotAStringNodeException.class)
    public void valueAsString_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null]";
        new JsonEditor(arrayJson).child(0).valueAsString();
    }

    @Test
    public void isNullValue_shouldCheckIfTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null, 1]";
        expect(new JsonEditor(arrayJson).child(0).isNullValue()).toBeTrue();
        expect(new JsonEditor(arrayJson).child(1).isNullValue()).toBeFalse();
    }

    @Test
    public void arraySetBoolean_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        expect(editor.root().child(0).valueAsNumber()).toEqual(1);

        editor.root().set(0, true);
        expect(editor.root().child(0).valueAsBoolean()).toBeTrue();

        editor.root().set(0, false);
        expect(editor.root().child(0).valueAsBoolean()).toBeFalse();
    }

    @Test
    public void arraySetInt_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 3);
        expect(editor.child(0).valueAsNumber()).toEqual(3);
    }

    @Test
    public void arraySetInt_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String arrayJson = "[1, 2, 3]";

        JsonEditor editor = new JsonEditor(arrayJson);
        expect(editor.set(0, 4).child(0).valueAsNumber()).toEqual(4);

        editor = new JsonEditor(arrayJson);
        editor.set(0, 4).set(1, 5);
        expect(editor.child(0).valueAsNumber()).toEqual(4);
        expect(editor.root().child(1).valueAsNumber()).toEqual(5);
    }

    @Test
    public void arraySetDouble_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 3.66);
        expect(editor.child(0).valueAsNumber()).toEqual(3.66);
    }

    @Test
    public void arraySetLong_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 344L);
        expect(editor.child(0).valueAsNumber()).toEqual(344L);
    }

    @Test
    public void arraySetString_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, "new value");
        expect(editor.child(0).valueAsString()).toEqual("new value");
    }

    @Test
    public void arraySetJsonEditor_shouldChangeTheValueAtTheGivenIndexInTheArray_basedOnTheCurrentPositionOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, new JsonEditor("[1, [2, 3]]").child(1));
        expect(editor.child(0).child(1).valueAsNumber()).toEqual(3);
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
        expect(new JsonEditor("{\"a\": 0}").set("a", 3).child("a").valueAsNumber()).toEqual(3);
    }

    @Test
    public void objectSetInt_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String objectJson = "{\"a\": 2, \"b\": 3}";

        JsonEditor editor = new JsonEditor(objectJson);
        expect(editor.set("a", 4).child("a").valueAsNumber()).toEqual(4);

        editor = new JsonEditor(objectJson);
        editor.set("a", 4).set("b", 5);
        expect(editor.child("a").valueAsNumber()).toEqual(4);
        expect(editor.root().child("b").valueAsNumber()).toEqual(5);
    }

    @Test
    public void objectSetLong_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        expect(new JsonEditor("{\"a\": 0}").set("a", 3L).child("a").valueAsNumber()).toEqual(3L);
    }

    @Test
    public void objectSetBoolean_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        expect(new JsonEditor("{\"a\": 0}").set("a", false).child("a").valueAsBoolean()).toBeFalse();
    }

    @Test
    public void objectSetDouble_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        expect(new JsonEditor("{\"a\": 0}").set("a", 9.55).child("a").valueAsNumber()).toEqual(9.55);
    }

    @Test
    public void objectSetString_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        expect(new JsonEditor("{\"a\": 0}").set("a", "new value").child("a").valueAsString()).toEqual("new value");
    }

    @Test
    public void objectSetJsonEditor_shouldChangeTheValueAtTheGivenIndexInTheArray_basedOnTheCurrentPositionOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0}");
        editor.set("a", new JsonEditor("[1, [2, 3]]").child(1));
        expect(editor.child("a").child(1).valueAsNumber()).toEqual(3);
        expect(editor.root().toJson()).toEqual("{\"a\":[2,3]}");
    }

    @Test
    public void objectSetJsonEditor_shouldNotCrash_whenCreatingAPotentialLoopInTheDom() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0}");
        expect(editor.set("a", editor).toJson()).toEqual("{\"a\":{\"a\":0}}");
    }

    @Test
    public void arrayRemove_shouldRemoveTheSpecifiedIndexFromTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[0, 1, 2]");
        expect(editor.remove(1).toJson()).toEqual("[0,2]");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arrayRemove_shouldThrowException_whenIndexIsOutOfBounds() throws Exception {
        JsonEditor editor = new JsonEditor("[0, 1, 2]");
        expect(editor.remove(5).toJson()).toEqual("[0,2]");
    }

    @Test
    public void objectRemove_shouldRemoveTheSpecifiedPropertyFromTheObject() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0, \"b\": 1}");
        expect(editor.remove("a").toJson()).toEqual("{\"b\":1}");
        expect(editor.remove("does not exist").toJson()).toEqual("{\"b\":1}");
    }

    @Test
    public void root_shouldReturnTheEditorBackToTheRootOfTheDomTree() throws Exception {
        String json = "[1, {\"a\": 2}]";
        expect(new JsonEditor(json).child(1).child("a").root().child(0).valueAsNumber()).toEqual(1);
    }

    @Test
    public void toJson_shouldConvertTheCurrentNodeOfTheDomToJson_whenThatNodeIsAnArrayOrObject() throws Exception {
        String json = "[1, {\"a\": 2}]";
        expect(new JsonEditor(json).child(1).child("a").root().toJson()).toEqual(json.replace(" ", ""));
        expect(new JsonEditor(json).child(1).toJson()).toEqual("{\"a\":2}");
    }

    @Test(expected = JsonEditor.NotAnArrayOrObjectNodeException.class)
    public void toJson_shouldThrowException_whenTheCurrentNodeIsNotAnArrayOrObject() throws Exception {
        new JsonEditor("[1, {\"a\": 2}]").child(1).child("a").toJson();
    }
}
