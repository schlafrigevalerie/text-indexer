package com.valerix.textindexer.core.models;

import java.util.List;
//результат поискового запроса
public class SearchResult {
    private final String query;
    private final List<IndexedFile> files;
    private final long executionTimeMs;

    public SearchResult(String query, List<IndexedFile> files, long executionTimeMs) {
        this.query = query;
        this.files = List.copyOf(files);
        this.executionTimeMs = executionTimeMs;
    }

    public String getQuery() { return query; }
    public List<IndexedFile> getFiles() { return files; }
    public long getExecutionTimeMs() { return executionTimeMs; }

    @Override
    public String toString() {
        return "SearchResult{query='%s', found=%d files, time=%dms}".formatted(
                query, files.size(), executionTimeMs);
    }
}