package com.valerix.textindexer.core.models;

import java.util.List;

public class SearchResult {
    private final String query;
    private final List<com.valerix.textindexer.core.model.IndexedFile> files;
    private final long executionTimeMs;

    public SearchResult(String query, List<com.valerix.textindexer.core.model.IndexedFile> files, long executionTimeMs) {
        this.query = query;
        this.files = List.copyOf(files); // защита от изменений снаружи
        this.executionTimeMs = executionTimeMs;
    }

    public String getQuery() { return query; }
    public List<com.valerix.textindexer.core.model.IndexedFile> getFiles() { return files; }
    public long getExecutionTimeMs() { return executionTimeMs; }

    @Override
    public String toString() {
        return "SearchResult{query='%s', found=%d files, time=%dms}".formatted(
                query, files.size(), executionTimeMs);
    }
}