package com.valerix.textindexer.core.index;

import com.valerix.textindexer.core.models.SearchResult;
import com.valerix.textindexer.core.tokenizer.SimpleTokenizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryIndexTest {

    private final SimpleTokenizer tokenizer = new SimpleTokenizer();
    private final Index index = new InMemoryIndex();

    @Test
    void testAddAndSearch(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Java is great and Python is cool");

        index.addFile(file, tokenizer);

        SearchResult result = index.search("java", tokenizer);

        assertEquals(1, result.getFiles().size());
    }

    @Test
    void testSearchNotFound() {
        SearchResult result = index.search("nonexistentword", tokenizer);
        assertTrue(result.getFiles().isEmpty());
    }
}