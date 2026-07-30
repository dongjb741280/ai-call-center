package com.voxai.cc.fs.esl.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HeaderParserTest {

    @Test
    void shouldSplitStandardHeader() {
        String[] result = HeaderParser.splitHeader("Content-Type: application/json");
        assertEquals("Content-Type", result[0]);
        assertEquals("application/json", result[1]);
    }

    @Test
    void shouldSplitHeaderWithExtraWhitespace() {
        String[] result = HeaderParser.splitHeader("  X-Custom  :   value123  ");
        assertEquals("X-Custom", result[0]);
        assertEquals("value123", result[1]);
    }

    @Test
    void shouldReturnEmptyValueWhenNoValueAfterColon() {
        String[] result = HeaderParser.splitHeader("Empty-Header:");
        assertEquals("Empty-Header", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    void shouldReturnEmptyValueForHeaderWithOnlyName() {
        String[] result = HeaderParser.splitHeader("OnlyName");
        assertEquals("OnlyName", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    void shouldSplitHeaderWithColonInValue() {
        String[] result = HeaderParser.splitHeader("Location: http://host:8080/path");
        assertEquals("Location", result[0]);
        assertEquals("http://host:8080/path", result[1]);
    }

    @Test
    void shouldReturnEmptyValueForEmptyString() {
        String[] result = HeaderParser.splitHeader("");
        assertEquals("", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    void shouldHandleWhitespaceOnlyHeader() {
        String[] result = HeaderParser.splitHeader("   ");
        assertEquals("", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    void shouldHandleHeaderWithTabWhitespace() {
        String[] result = HeaderParser.splitHeader("Key\t:\tValue\t");
        assertEquals("Key", result[0]);
        assertEquals("Value", result[1]);
    }
}
