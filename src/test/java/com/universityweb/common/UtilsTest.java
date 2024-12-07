package com.universityweb.common;

import com.universityweb.common.util.Utils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    public void testEqualLists_SameWords() {
        List<String> list1 = Arrays.asList("Hello world", "Java programming");
        List<String> list2 = Arrays.asList("Hello world", "Java programming");
        assertTrue(Utils.isEquals(list1, list2));
    }

    @Test
    public void testEqualLists_DifferentWhitespace() {
        List<String> list1 = Arrays.asList("Hello  world", "Java   programming");
        List<String> list2 = Arrays.asList("Hello world", "Java programming");
        assertTrue(Utils.isEquals(list1, list2));
    }

    @Test
    public void testUnequalLists_DifferentWordCounts() {
        List<String> list1 = Arrays.asList("Hello world", "Java programming language");
        List<String> list2 = Arrays.asList("Hello world", "Java programming");
        assertFalse(Utils.isEquals(list1, list2));
    }

    @Test
    public void testUnequalLists_DifferentWords() {
        List<String> list1 = Arrays.asList("Hello world", "Java programming");
        List<String> list2 = Arrays.asList("Hello world", "Python programming");
        assertFalse(Utils.isEquals(list1, list2));
    }

    @Test
    public void testUnequalLists_DifferentOrder() {
        List<String> list1 = Arrays.asList("Java programming", "Hello world");
        List<String> list2 = Arrays.asList("Hello world", "Java programming");
        assertFalse(Utils.isEquals(list1, list2));
    }

    @Test
    public void testEqualLists_EmptyLists() {
        List<String> list1 = Collections.emptyList();
        List<String> list2 = Collections.emptyList();
        assertTrue(Utils.isEquals(list1, list2));
    }

    @Test
    public void testUnequalLists_OneEmptyList() {
        List<String> list1 = Arrays.asList("Hello world", "Java programming");
        List<String> list2 = Collections.emptyList();
        assertFalse(Utils.isEquals(list1, list2));
    }

    @Test
    public void testNullLists() {
        assertFalse(Utils.isEquals(null, Arrays.asList("Hello world")));
        assertFalse(Utils.isEquals(Arrays.asList("Hello world"), null));
        assertTrue(Utils.isEquals(null, null));
    }
}