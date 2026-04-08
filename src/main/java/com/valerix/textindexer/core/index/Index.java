package com.valerix.textindexer.core.index;

import com.valerix.textindexer.core.models.IndexedFile;
import com.valerix.textindexer.core.models.SearchResult;
import com.valerix.textindexer.core.tokenizer.Tokenizer;

import java.nio.file.Path;
import java.util.Collection;

public interface Index {
    void addFile(Path path, Tokenizer tokenizer);
    void addDirectory(Path directory, Tokenizer tokenizer, boolean recursive);
    void removeFile(Path path);
    SearchResult search(String query, Tokenizer tokenizer);
    Collection<IndexedFile> getAllIndexedFiles();
    void clear();
}