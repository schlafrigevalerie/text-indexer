package com.valerix.textindexer.core.tokenizer;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SimpleTokenizerTest {

    @Test
    void testSimpleSplit() {
        Tokenizer tokenizer = new SimpleTokenizer();
        List<String> tokens = tokenizer.tokenize("Hello, World!");

        assertEquals(2, tokens.size());
        assertEquals("hello", tokens.get(0));
        assertEquals("world", tokens.get(1));
    }

    @Test
    void testEmptyString() {
        Tokenizer tokenizer = new SimpleTokenizer();
        List<String> tokens = tokenizer.tokenize("   ");
        assertTrue(tokens.isEmpty());
    }
}