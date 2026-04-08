package com.valerix.textindexer.core.model;

import java.nio.file.Path;
import java.util.Objects;

public class IndexedFile {
    private final Path path;
    private final long size;
    private final long lastModified;

    public IndexedFile(Path path) {
        this.path = path.toAbsolutePath().normalize();
        this.size = path.toFile().length();
        this.lastModified = path.toFile().lastModified();
    }

    // Конструктор для обновления метаданных (при изменении файла)
    public IndexedFile(Path path, long size, long lastModified) {
        this.path = path.toAbsolutePath().normalize();
        this.size = size;
        this.lastModified = lastModified;
    }

    public Path getPath() { return path; }
    public long getSize() { return size; }
    public long getLastModified() { return lastModified; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexedFile that)) return false;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return path.toString();
    }
}