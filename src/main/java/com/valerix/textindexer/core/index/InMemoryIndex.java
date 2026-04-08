package com.valerix.textindexer.core.index;

import com.valerix.textindexer.core.models.IndexedFile;
import com.valerix.textindexer.core.models.SearchResult;
import com.valerix.textindexer.core.tokenizer.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

public class InMemoryIndex implements Index {
    private final Map<String, Set<IndexedFile>> invertedIndex = new ConcurrentHashMap<>();
    private final Map<Path, IndexedFile> fileRegistry = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void addFile(Path path, Tokenizer tokenizer) {
        if (!Files.isRegularFile(path)) {
            return;
        }

        try {
            String content = Files.readString(path);
            List<String> tokens = tokenizer.tokenize(content);
            IndexedFile indexedFile = new IndexedFile(path);

            lock.writeLock().lock();
            try {
                removeFileInternal(path);

                for (String token : tokens) {
                    invertedIndex
                            .computeIfAbsent(token, k -> ConcurrentHashMap.newKeySet())
                            .add(indexedFile);
                }
                fileRegistry.put(path, indexedFile);
            } finally {
                lock.writeLock().unlock();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    @Override
    public void addDirectory(Path directory, Tokenizer tokenizer, boolean recursive) {
        try (Stream<Path> paths = recursive ? Files.walk(directory) : Files.list(directory)) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> addFile(path, tokenizer));
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan directory: " + directory, e);
        }
    }

    @Override
    public void removeFile(Path path) {
        lock.writeLock().lock();
        try {
            removeFileInternal(path);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void removeFileInternal(Path path) {
        IndexedFile removed = fileRegistry.remove(path);
        if (removed != null) {
            for (Set<IndexedFile> files : invertedIndex.values()) {
                files.remove(removed);
            }
            invertedIndex.values().removeIf(Set::isEmpty);
        }
    }

    @Override
    public SearchResult search(String query, Tokenizer tokenizer) {
        long startTime = System.currentTimeMillis();
        List<String> tokens = tokenizer.tokenize(query);

        lock.readLock().lock();
        try {
            Set<IndexedFile> resultFiles = new HashSet<>();
            for (String token : tokens) {
                Set<IndexedFile> files = invertedIndex.get(token);
                if (files != null) {
                    resultFiles.addAll(files);
                }
            }
            long duration = System.currentTimeMillis() - startTime;
            return new SearchResult(query, new ArrayList<com.valerix.textindexer.core.models.IndexedFile>(resultFiles), duration);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Collection<IndexedFile> getAllIndexedFiles() {
        lock.readLock().lock();
        try {
            return List.copyOf(fileRegistry.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            invertedIndex.clear();
            fileRegistry.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}