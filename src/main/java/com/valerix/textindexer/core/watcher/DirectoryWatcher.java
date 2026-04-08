package com.valerix.textindexer.core.watcher;

import com.valerix.textindexer.core.index.Index;
import com.valerix.textindexer.core.tokenizer.Tokenizer;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class DirectoryWatcher {
    private final Index index;
    private final Tokenizer tokenizer;
    private final WatchService watchService;
    private final Map<WatchKey, Path> registeredKeys;
    private final ExecutorService executor;

    private volatile boolean running = false;

    public DirectoryWatcher(Index index, Tokenizer tokenizer) {
        this.index = index;
        this.tokenizer = tokenizer;
        this.registeredKeys = new HashMap<>();
        this.executor = Executors.newSingleThreadExecutor();

        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create WatchService", e);
        }
    }

    /**
     * начать наблюдение за директорией.
     * @param directory путь к директории
     * @param recursive если true — рекурсивно подписаться на поддиректории
     */
    public void watch(Path directory, boolean recursive) throws IOException {
        register(directory);

        if (recursive) {
            try (Stream<Path> paths = Files.walk(directory)) {
                paths.filter(Files::isDirectory)
                        .filter(p -> !p.equals(directory))
                        .forEach(this::registerSilent);
            }
        }
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        registeredKeys.put(key, dir);
    }

    private void registerSilent(Path dir) {
        try {
            register(dir);
        } catch (IOException ignored) {
            // Пропускаем директории, которые нельзя отслеживать
        }
    }

    public void start() {
        if (running) return;
        running = true;

        executor.submit(() -> {
            while (running) {
                try {
                    WatchKey key = watchService.take();
                    Path dir = registeredKeys.get(key);
                    if (dir == null) continue;

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path changed = dir.resolve(ev.context());

                        handleEvent(event.kind(), changed);
                    }
                    key.reset();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private void handleEvent(WatchEvent.Kind<?> kind, Path path) {
        if (!Files.isRegularFile(path)) return;

        if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            index.removeFile(path);
        } else if (kind == StandardWatchEventKinds.ENTRY_CREATE ||
                kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            // Небольшая задержка, чтобы файл точно успел записаться
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            index.addFile(path, tokenizer);
        }
    }

    public void stop() {
        running = false;
        executor.shutdownNow();
        try {
            watchService.close();
        } catch (IOException ignored) {}
    }
}