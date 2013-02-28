package com.example.android.sampleapp.test.support;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class JsonEditorTest {

    @Test
    public void exampleOfUsingThisClassToEditJson() throws Exception {
        String json = "[1, 2, {\"a\": [[7, 8], 3, 4], \"b\": 10}, 5, 999]";
        String expectedEditedJson = "[1.5,\"hello\",{\"a\":[[true,8],3,4],\"b\":1,\"new_prop\":\"value\"},[\"more_json\"]]";

        // extreme chaining example
        String editedJson = new JsonEditor(json)
                .set(0, 1.5)
                .set(1, "hello")
                .child(2) // move the current position of the editor to the third child
                .set("b", 1) // set an existing property; throws an exception if the property does not exist
                .put("new_prop", "value") // put a property that does not exist
                .child("a").child(0) // move deeper in the tree
                .set(0, true)
                .root() // move back to the root node
                .set(3, new JsonEditor("[\"more_json\"]"))
                .remove(4)
                .toJson();

        assertThat(editedJson).isEqualTo(expectedEditedJson);

        // same example with less chaining
        JsonEditor e = new JsonEditor(json);
        e.root().set(0, 1.5);
        e.root().set(1, "hello");
        e.root().child(2).put("new_prop", "value").set("b", 1);
        e.root().child(2).child("a").child(0).set(0, true);
        e.root().set(3, new JsonEditor("[\"more_json\"]"));
        e.root().remove(4);

        assertThat(e.toJson()).isEqualTo(expectedEditedJson);
    }

    @Test(expected = JsonEditor.JsonEditorException.class)
    public void constructor_shouldThrowException_whenJsonIsInvalid() throws Exception {
        new JsonEditor("this is not valid json");
    }

    @Test(expected = JsonEditor.JsonEditorException.class)
    public void constructor_shouldThrowException_whenJsonIsInvalid2() throws Exception {
        new JsonEditor("42");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arrayChild_shouldThrowException_whenArrayIsEmpty() throws Exception {
        new JsonEditor("[]").child(0);
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arrayChild_shouldThrowException_whenItIsUsedOnAnObject() throws Exception {
        new JsonEditor("{}").child(0);
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arrayChild_shouldThrowException_whenItIsUsedOnABasicDataType() throws Exception {
        new JsonEditor("[1]").child(0).child(0);
    }

    @Test
    public void arrayChild_shouldMoveTheCurrentNodeToAnElementOfTheArray() throws Exception {
        String arrayJson = "[1, 2, 3]";
        assertThat(new JsonEditor(arrayJson).child(0).valueAsNumber()).isEqualTo(1);
        assertThat(new JsonEditor(arrayJson).child(2).valueAsNumber()).isEqualTo(3);
    }

    @Test
    public void arrayChild_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String arrayJson = "[[1, 2, 3], 4, 5]";
        assertThat(new JsonEditor(arrayJson).child(0).child(0).valueAsNumber()).isEqualTo(1);
        assertThat(new JsonEditor(arrayJson).child(0).child(2).valueAsNumber()).isEqualTo(3);

        String deeperArrayJson = "[[11, 22, [111, 222, [1111, 2222]]], 2, 3]";
        assertThat(new JsonEditor(deeperArrayJson).child(0).child(2).child(1).valueAsNumber()).isEqualTo(222);
        assertThat(new JsonEditor(deeperArrayJson).child(0).child(2).child(2).child(1).valueAsNumber()).isEqualTo(2222);
    }

    @Test(expected = JsonEditor.NotAnObjectNodeException.class)
    public void objectChild_shouldThrowException_whenNodeIsNotAnObject() throws Exception {
        new JsonEditor("[]").child("property_name");
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectChild_shouldThrowException_whenPropertyDoesNotExistOnObject() throws Exception {
        new JsonEditor("{\"a\": 1}").child("this property does not exist");
    }

    @Test
    public void objectChild_shouldMoveTheCurrentNodeToTheValueOfThatPropertyOfTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 1}").child("a").valueAsNumber()).isEqualTo(1);
    }

    @Test
    public void objectChild_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String objectJson = "{\"a\": {\"aa\": 42}}";
        assertThat(new JsonEditor(objectJson).child("a").child("aa").valueAsNumber()).isEqualTo(42);

        String nestedObjectJson = "{\"a\": 1, \"b\": {\"aa\": 22, \"bb\": {\"aaa\": 555, \"bbb\": 777}}}";
        assertThat(new JsonEditor(nestedObjectJson).child("b").child("bb").child("aaa").valueAsNumber()).isEqualTo(555);
    }

    @Test
    public void child_shouldSupportChainedCalls_mixingArrayChildAndObjectChild() throws Exception {
        String arrayJson = "[{\"a\": {\"aa\": [42, 99]}}]";
        assertThat(new JsonEditor(arrayJson).child(0).child("a").child("aa").child(1).valueAsNumber()).isEqualTo(99);
    }

    @Test
    public void valueAsNumber_shouldReturnNumberForNumericNodes() throws Exception {
        String arrayJson = "[1.5, 2.1, 3.9]";
        assertThat(new JsonEditor(arrayJson).child(0).valueAsNumber()).isEqualTo(1.5);
        assertThat(new JsonEditor(arrayJson).child(2).valueAsNumber()).isEqualTo(3.9);
    }

    @Test(expected = JsonEditor.NotANumericNodeException.class)
    public void valueAsNumber_shouldThrowException_whenTheNodeIsNotANumericNode() throws Exception {
        new JsonEditor("[false]").child(0).valueAsNumber();
    }

    @Test(expected = JsonEditor.NotANumericNodeException.class)
    public void valueAsNumber_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        new JsonEditor("[null]").child(0).valueAsNumber();
    }

    @Test
    public void valueAsBoolean_shouldReturnBooleanForBooleanNodes() throws Exception {
        String arrayJson = "[false, false, true]";
        assertThat(new JsonEditor(arrayJson).child(0).valueAsBoolean()).isFalse();
        assertThat(new JsonEditor(arrayJson).child(2).valueAsBoolean()).isTrue();
    }

    @Test(expected = JsonEditor.NotABooleanNodeException.class)
    public void valueAsBoolean_shouldThrowException_whenTheNodeIsNotABooleanNode() throws Exception {
        new JsonEditor("[1]").child(0).valueAsBoolean();
    }

    @Test(expected = JsonEditor.NotABooleanNodeException.class)
    public void valueAsBoolean_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        new JsonEditor("[null]").child(0).valueAsBoolean();
    }

    @Test
    public void valueAsString_shouldReturnStringForBooleanNodes() throws Exception {
        String arrayJson = "[\"string one\", \"string two\", \"string three\"]";
        assertThat(new JsonEditor(arrayJson).child(0).valueAsString()).isEqualTo("string one");
        assertThat(new JsonEditor(arrayJson).child(2).valueAsString()).isEqualTo("string three");
    }

    @Test(expected = JsonEditor.NotAStringNodeException.class)
    public void valueAsString_shouldThrowException_whenTheNodeIsNotAStringNode() throws Exception {
        new JsonEditor("[1]").child(0).valueAsString();
    }

    @Test(expected = JsonEditor.NotAStringNodeException.class)
    public void valueAsString_shouldThrowException_whenTheNodeIsANullNode() throws Exception {
        new JsonEditor("[null]").child(0).valueAsString();
    }

    @Test
    public void isNullValue_shouldCheckIfTheNodeIsANullNode() throws Exception {
        String arrayJson = "[null, 1]";
        assertThat(new JsonEditor(arrayJson).child(0).isNullValue()).isTrue();
        assertThat(new JsonEditor(arrayJson).child(1).isNullValue()).isFalse();
    }

    @Test
    public void arraySetBoolean_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        assertThat(editor.root().child(0).valueAsNumber()).isEqualTo(1);

        editor.root().set(0, true);
        assertThat(editor.root().child(0).valueAsBoolean()).isTrue();

        editor.root().set(0, false);
        assertThat(editor.root().child(0).valueAsBoolean()).isFalse();
    }

    @Test
    public void arraySetInt_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 3);
        assertThat(editor.child(0).valueAsNumber()).isEqualTo(3);
    }

    @Test
    public void arraySetInt_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String arrayJson = "[1, 2, 3]";

        JsonEditor editor = new JsonEditor(arrayJson);
        assertThat(editor.set(0, 4).child(0).valueAsNumber()).isEqualTo(4);

        editor = new JsonEditor(arrayJson);
        editor.set(0, 4).set(1, 5);
        assertThat(editor.child(0).valueAsNumber()).isEqualTo(4);
        assertThat(editor.root().child(1).valueAsNumber()).isEqualTo(5);
    }

    @Test
    public void arraySetDouble_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 3.66);
        assertThat(editor.child(0).valueAsNumber()).isEqualTo(3.66);
    }

    @Test
    public void arraySetLong_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, 344L);
        assertThat(editor.child(0).valueAsNumber()).isEqualTo(344L);
    }

    @Test
    public void arraySetString_shouldChangeTheValueAtTheGivenIndexInTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, "new value");
        assertThat(editor.child(0).valueAsString()).isEqualTo("new value");
    }

    @Test
    public void arraySetJsonEditor_shouldChangeTheValueAtTheGivenIndexInTheArray_basedOnTheCurrentPositionOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.set(0, new JsonEditor("[1, [2, 3]]").child(1));
        assertThat(editor.child(0).child(1).valueAsNumber()).isEqualTo(3);
        assertThat(editor.root().toJson()).isEqualTo("[[2,3]]");
    }

    @Test
    public void arraySetJsonEditor_shouldNotCrash_whenCreatingAPotentialLoopInTheDom() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        assertThat(editor.set(0, editor).toJson()).isEqualTo("[[1]]");
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arraySet_shouldThrowException_whenItIsNotUsedOnAnArray() throws Exception {
        new JsonEditor("{}").set(0, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arraySet_shouldThrowException_whenArrayIsEmpty() throws Exception {
        new JsonEditor("[]").set(0, 2);
    }

    @Test(expected = JsonEditor.NotAnObjectNodeException.class)
    public void objectSetInt_shouldThrowException_whenItIsNotUsedOnAnObject() throws Exception {
        new JsonEditor("[]").set("a", 2);
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectSetInt_shouldThrowException_whenPropertyDoesNotExist() throws Exception {
        new JsonEditor("{}").set("a", 2);
    }

    @Test
    public void objectSetInt_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").set("a", 3).child("a").valueAsNumber()).isEqualTo(3);
    }

    @Test
    public void objectSetInt_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String objectJson = "{\"a\": 2, \"b\": 3}";

        JsonEditor editor = new JsonEditor(objectJson);
        assertThat(editor.set("a", 4).child("a").valueAsNumber()).isEqualTo(4);

        editor = new JsonEditor(objectJson);
        editor.set("a", 4).set("b", 5);
        assertThat(editor.child("a").valueAsNumber()).isEqualTo(4);
        assertThat(editor.root().child("b").valueAsNumber()).isEqualTo(5);
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectSetLong_shouldThrowException_whenPropertyDoesNotExist() throws Exception {
        new JsonEditor("{}").set("a", 2L);
    }

    @Test
    public void objectSetLong_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").set("a", 3L).child("a").valueAsNumber()).isEqualTo(3L);
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectSetDouble_shouldThrowException_whenPropertyDoesNotExist() throws Exception {
        new JsonEditor("{}").set("a", 9.55);
    }

    @Test
    public void objectSetDouble_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").set("a", 9.55).child("a").valueAsNumber()).isEqualTo(9.55);
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectSetBoolean_shouldThrowException_whenPropertyDoesNotExist() throws Exception {
        new JsonEditor("{}").set("a", false);
    }

    @Test
    public void objectSetBoolean_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").set("a", false).child("a").valueAsBoolean()).isFalse();
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectSetString_shouldThrowException_whenPropertyDoesNotExist() throws Exception {
        new JsonEditor("{}").set("a", "new value");
    }

    @Test
    public void objectSetString_shouldChangeTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").set("a", "new value").child("a").valueAsString()).isEqualTo("new value");
    }

    @Test(expected = JsonEditor.NoSuchPropertyException.class)
    public void objectSetJsonEditor_shouldThrowException_whenPropertyDoesNotExist() throws Exception {
        new JsonEditor("{}").set("a", new JsonEditor("[1, [2, 3]]"));
    }

    @Test
    public void objectSetJsonEditor_shouldChangeTheValueAtTheGivenPropertyInTheObject_basedOnTheFocusedNodeOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0}");

        editor.set("a", new JsonEditor("[1, [2, 3]]").child(1));
        assertThat(editor.child("a").child(1).valueAsNumber()).isEqualTo(3);
        assertThat(editor.root().toJson()).isEqualTo("{\"a\":[2,3]}");
    }

    @Test
    public void objectSetJsonEditor_shouldNotCrash_whenCreatingAPotentialLoopInTheDom() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0}");
        assertThat(editor.set("a", editor).toJson()).isEqualTo("{\"a\":{\"a\":0}}");
    }

    @Test(expected = JsonEditor.NotAnObjectNodeException.class)
    public void objectPutInt_shouldThrowException_whenItIsNotUsedOnAnObject() throws Exception {
        new JsonEditor("[]").put("a", 2);
    }

    @Test
    public void objectPutInt_shouldPutTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").put("a", 3).child("a").valueAsNumber()).isEqualTo(3);
        assertThat(new JsonEditor("{\"a\": 0}").put("no such property", 4).child("no such property").valueAsNumber()).isEqualTo(4);
    }

    @Test
    public void objectPutInt_shouldSupportChainedCalls_toTraverseTheDomTree() throws Exception {
        String objectJson = "{\"a\": 2, \"b\": 3}";

        JsonEditor editor = new JsonEditor(objectJson);
        assertThat(editor.put("a", 4).child("a").valueAsNumber()).isEqualTo(4);

        editor = new JsonEditor(objectJson);
        editor.put("a", 4).put("b", 5);
        assertThat(editor.child("a").valueAsNumber()).isEqualTo(4);
        assertThat(editor.root().child("b").valueAsNumber()).isEqualTo(5);
    }

    @Test
    public void objectPutLong_shouldPutTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").put("a", 3L).child("a").valueAsNumber()).isEqualTo(3L);
        assertThat(new JsonEditor("{\"a\": 0}").put("no such property", 4L).child("no such property").valueAsNumber()).isEqualTo(4L);
    }

    @Test
    public void objectPutBoolean_shouldPutTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").put("a", false).child("a").valueAsBoolean()).isFalse();
        assertThat(new JsonEditor("{\"a\": 0}").put("no such property", true).child("no such property").valueAsBoolean()).isTrue();
    }

    @Test
    public void objectPutDouble_shouldPutTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").put("a", 9.55).child("a").valueAsNumber()).isEqualTo(9.55);
        assertThat(new JsonEditor("{\"a\": 0}").put("no such property", 999.55).child("no such property").valueAsNumber()).isEqualTo(999.55);
    }

    @Test
    public void objectPutString_shouldPutTheValueAtTheGivenPropertyInTheObject() throws Exception {
        assertThat(new JsonEditor("{\"a\": 0}").put("a", "new value").child("a").valueAsString()).isEqualTo("new value");
        assertThat(new JsonEditor("{\"a\": 0}").put("no such property", "String value").child("no such property").valueAsString()).isEqualTo("String value");
    }

    @Test
    public void objectPutJsonEditor_shouldPutTheValueAtTheGivenPropertyInTheObject_basedOnTheFocusedNodeOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0}");

        editor.put("a", new JsonEditor("[1, [2, 3]]").child(1));
        assertThat(editor.child("a").child(1).valueAsNumber()).isEqualTo(3);
        assertThat(editor.root().toJson()).isEqualTo("{\"a\":[2,3]}");

        editor = new JsonEditor("{\"a\": 0}");
        editor.put("no such property", new JsonEditor("[111, [2222, 3333]]").child(1));
        assertThat(editor.child("no such property").child(0).valueAsNumber()).isEqualTo(2222);
        assertThat(editor.root().toJson()).isEqualTo("{\"a\":0,\"no such property\":[2222,3333]}");

    }

    @Test
    public void objectPutJsonEditor_shouldNotCrash_whenCreatingAPotentialLoopInTheDom() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0}");
        assertThat(editor.put("a", editor).toJson()).isEqualTo("{\"a\":{\"a\":0}}");
    }

    @Test
    public void arrayRemove_shouldRemoveTheSpecifiedIndexFromTheArray() throws Exception {
        JsonEditor editor = new JsonEditor("[0, 1, 2]");
        assertThat(editor.remove(1).toJson()).isEqualTo("[0,2]");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arrayRemove_shouldThrowException_whenIndexIsOutOfBounds() throws Exception {
        JsonEditor editor = new JsonEditor("[0, 1, 2]");
        assertThat(editor.remove(5).toJson()).isEqualTo("[0,2]");
    }

    @Test
    public void objectRemove_shouldRemoveTheSpecifiedPropertyFromTheObject() throws Exception {
        JsonEditor editor = new JsonEditor("{\"a\": 0, \"b\": 1}");
        assertThat(editor.remove("a").toJson()).isEqualTo("{\"b\":1}");
        assertThat(editor.remove("does not exist").toJson()).isEqualTo("{\"b\":1}");
    }

    @Test
    public void root_shouldReturnTheEditorBackToTheRootOfTheDomTree() throws Exception {
        String json = "[1, {\"a\": 2}]";
        assertThat(new JsonEditor(json).child(1).child("a").root().child(0).valueAsNumber()).isEqualTo(1);
    }

    @Test
    public void toJson_shouldConvertTheCurrentNodeOfTheDomToJson_whenThatNodeIsAnArrayOrObject() throws Exception {
        String json = "[1, {\"a\": 2}]";
        assertThat(new JsonEditor(json).child(1).child("a").root().toJson()).isEqualTo(json.replace(" ", ""));
        assertThat(new JsonEditor(json).child(1).toJson()).isEqualTo("{\"a\":2}");
    }

    @Test(expected = JsonEditor.NotAnArrayOrObjectNodeException.class)
    public void toJson_shouldThrowException_whenTheCurrentNodeIsNotAnArrayOrObject() throws Exception {
        new JsonEditor("[1, {\"a\": 2}]").child(1).child("a").toJson();
    }

    @Test
    public void arrayAppendInt_shouldAddAnElementToTheEndOfAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").append(42).root().toJson()).isEqualTo("{\"a\":[1,2,42]}");
    }

    @Test
    public void arrayAppendString_shouldAddAnElementToTheEndOfAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").append("foo").root().toJson()).isEqualTo("{\"a\":[1,2,\"foo\"]}");
    }

    @Test
    public void arrayAppendBoolean_shouldAddAnElementToTheEndOfAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").append(true).root().toJson()).isEqualTo("{\"a\":[1,2,true]}");
    }

    @Test
    public void arrayAppendDouble_shouldAddAnElementToTheEndOfAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").append(42.2).root().toJson()).isEqualTo("{\"a\":[1,2,42.2]}");
    }

    @Test
    public void arrayAppendLong_shouldAddAnElementToTheEndOfAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").append(42L).root().toJson()).isEqualTo("{\"a\":[1,2,42]}");
    }

    @Test
    public void arrayAppendJsonEditor_shouldAppendTheValueToTheEndOfTheArray_basedOnTheCurrentPositionOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.append(new JsonEditor("[1, [2, 3]]").child(1)).root();
        assertThat(editor.toJson()).isEqualTo("[1,[2,3]]");
    }

    @Test
    public void arrayAppendJsonEditor_shouldAppendValueNodesToTheEndOfTheArray_basedOnTheCurrentPositionOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.append(new JsonEditor("[2]").child(0)).root();
        assertThat(editor.toJson()).isEqualTo("[1,2]");
    }

    @Test
    public void arrayInsertJsonEditor_shouldInsertTheValueAtTheGivenIndexInTheArray_basedOnTheCurrentPositionOfTheEditor() throws Exception {
        JsonEditor editor = new JsonEditor("[1]");
        editor.insert(0, new JsonEditor("[1, [2, 3]]").child(1)).root();
        assertThat(editor.toJson()).isEqualTo("[[2,3],1]");
    }

    @Test
    public void arrayInsertString_shouldInsertAnElementIntoAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").insert(1, "foo").root().toJson()).isEqualTo("{\"a\":[1,\"foo\",2]}");
    }

    @Test
    public void arrayInsertBoolean_shouldInsertAnElementIntoAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").insert(1, true).root().toJson()).isEqualTo("{\"a\":[1,true,2]}");
    }

    @Test
    public void arrayInsertDouble_shouldInsertAnElementIntoAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").insert(1, 42.2).root().toJson()).isEqualTo("{\"a\":[1,42.2,2]}");
    }

    @Test
    public void arrayInsertLong_shouldInsertAnElementIntoAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 2]}");
        assertThat(editor.child("a").insert(1, 42L).root().toJson()).isEqualTo("{\"a\":[1,42,2]}");
    }

    @Test
    public void arrayInsertInt_shouldInsertAnElementIntoAnArray() {
        JsonEditor editor = new JsonEditor("{\"a\": [1, 3, 4]}");
        assertThat(editor.child("a").insert(1, 2).root().toJson()).isEqualTo("{\"a\":[1,2,3,4]}");
        assertThat(new JsonEditor("[]").insert(0, 44).root().toJson()).isEqualTo("[44]");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arrayInsertInt_withIndexTooLarge_shouldThrow() {
        new JsonEditor("[1, 2]").insert(2, 2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void arrayInsertInt_withIndexBelowZero_shouldThrow() {
        new JsonEditor("[1, 2]").insert(-1, 2);
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arrayInsertInt_onAnObject_shouldThrow() {
        new JsonEditor("{}").insert(0, 2);
    }

    @Test
    public void arrayLength_shouldReturnTheLengthOfAnArray() {
        assertThat(new JsonEditor("[]").length()).isEqualTo(0);
        assertThat(new JsonEditor("{\"a\": [1, 2, 3, 4]}").child("a").length()).isEqualTo(4);
    }

    @Test(expected = JsonEditor.NotAnArrayNodeException.class)
    public void arrayLength_onAnObject_shouldThrow() {
        new JsonEditor("{}").length();
    }

    @Test
    public void isArray_shouldReturnTrueOnlyForArrays() {
        JsonEditor editor = new JsonEditor("{\"a\": [{}, 2]}");
        assertThat(editor.child("a").isArray()).isTrue();
        assertThat(editor.root().child("a").child(0).isArray()).isFalse();
        assertThat(editor.root().child("a").child(1).isArray()).isFalse();
    }

    @Test
    public void isObject_shouldReturnTrueOnlyForObjects() {
        JsonEditor editor = new JsonEditor("{\"a\": [{}, 2]}");
        assertThat(editor.isObject()).isTrue();
        assertThat(editor.child("a").isObject()).isFalse();
    }

    @Test(expected = JsonEditor.RootNodeHasNoParent.class)
    public void parent_whenUsedOnRootNode_shouldThrow() {
        new JsonEditor("[]").parent();
    }

    @Test(expected = JsonEditor.RootNodeHasNoParent.class)
    public void parent_whenUsedOnRootNodeAfterTraversingBackAndForth_shouldThrow() {
        String jsonString = "{\"a\":[1,{\"b\":42}],\"c\":[[44]]}";
        JsonEditor editor = new JsonEditor(jsonString);
        editor.root().child("c").child(0).child(0).root().child("a").parent().parent();
    }

    @Test
    public void parent_shouldNavigateBackToTheParentOfTheCurrentNode() {
        String jsonString = "{\"a\":[1,{\"b\":42}],\"c\":[[44]]}";
        JsonEditor editor = new JsonEditor(jsonString);
        assertThat(editor.child("a").parent().toJson()).isEqualTo(jsonString);
        assertThat(editor.root().child("a").child(0).parent().toJson()).isEqualTo("[1,{\"b\":42}]");
        assertThat(editor.root().child("a").child(1).child("b").parent().toJson()).isEqualTo("{\"b\":42}");
    }

    @Test
    public void removeAll_shouldClearObjectNodesAndArrayNodes() {
        assertThat(new JsonEditor("[]").removeAll().toJson()).isEqualTo("[]");
        assertThat(new JsonEditor("[1, 2]").removeAll().toJson()).isEqualTo("[]");
        assertThat(new JsonEditor("{}").removeAll().toJson()).isEqualTo("{}");
        assertThat(new JsonEditor("{\"a\": 1}").removeAll().toJson()).isEqualTo("{}");
    }

    @Test(expected = JsonEditor.NotAnArrayOrObjectNodeException.class)
    public void removeAll_whenUsedOnNonContainer_shouldThrow() {
        new JsonEditor("[42]").child(0).removeAll();
    }

    @Test(expected = JsonEditor.NotAnObjectNodeException.class)
    public void keySet_whenUsedOnNonObject_shouldThrow() {
        new JsonEditor("[42]").child(0).keySet();
    }

    @Test
    public void keySet_shouldReturnAllKeysAsASet() {
        assertThat(new JsonEditor("{\"c\":1, \"a\":2, \"b\":3}").keySet()).contains("a", "b", "c");
        assertThat(new JsonEditor("{}").keySet()).isEmpty();
    }

    @Test
    public void toString_shouldShowPathToCurrentNode() {
        String jsonString = "{\"a\":[1,{\"b\":42}],\"c\":[[44]]}";
        JsonEditor editor = new JsonEditor(jsonString);
        assertThat(editor.toString()).contains("focus=ROOT}");
        assertThat(editor.child("a").toString()).contains("focus=ROOT['a']}");
        assertThat(editor.child(0).toString()).contains("focus=ROOT['a'][0]}");
        assertThat(editor.parent().toString()).contains("focus=ROOT['a']}");
        assertThat(editor.root().toString()).contains("focus=ROOT}");
    }

}
