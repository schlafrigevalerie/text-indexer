package com.valerix.textindexer.app;

import com.valerix.textindexer.core.index.Index;
import com.valerix.textindexer.core.index.InMemoryIndex;
import com.valerix.textindexer.core.models.IndexedFile;
import com.valerix.textindexer.core.models.SearchResult;
import com.valerix.textindexer.core.tokenizer.SimpleTokenizer;
import com.valerix.textindexer.core.tokenizer.Tokenizer;
import com.valerix.textindexer.core.watcher.DirectoryWatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Консольное приложение для работы с текстовым индексатором.
 */
public class Main {
    private static final Tokenizer TOKENIZER = new SimpleTokenizer();
    private static final Index index = new InMemoryIndex();
    private static DirectoryWatcher watcher;

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String command = args[0];

        try {
            if ("--add".equals(command)) {
                handleAdd(args);
            } else if ("--search".equals(command)) {
                handleSearch(args);
            } else if ("--watch".equals(command)) {
                handleWatch(args);
            } else if ("--list".equals(command)) {
                handleList();
            } else if ("--clear".equals(command)) {
                handleClear();
            } else if ("--help".equals(command) || "-h".equals(command)) {
                printHelp();
            } else {
                System.err.println("Unknown command: " + command);
                printHelp();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleAdd(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: --add <path> [--recursive]");
            return;
        }

        Path path = Paths.get(args[1]);
        boolean recursive = Arrays.asList(args).contains("--recursive");

        if (path.toFile().isDirectory()) {
            System.out.println("Indexing directory: " + path);
            index.addDirectory(path, TOKENIZER, recursive);
        } else {
            System.out.println("Indexing file: " + path);
            index.addFile(path, TOKENIZER);
        }

        System.out.println("Indexed files: " + index.getAllIndexedFiles().size());
    }

    private static void handleSearch(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: --search <word>");
            return;
        }

        String query = args[1];
        System.out.println("Searching for: " + query);

        SearchResult result = index.search(query, TOKENIZER);

        if (result.getFiles().isEmpty()) {
            System.out.println("No files found containing '" + query + "'");
        } else {
            System.out.println("Found in " + result.getFiles().size() + " file(s):");
            for (IndexedFile file : result.getFiles()) {
                System.out.println("  - " + file.getPath());
            }
        }
        System.out.println("Search time: " + result.getExecutionTimeMs() + "ms");
    }

    private static void handleWatch(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: --watch <path> [--recursive]");
            return;
        }

        Path path = Paths.get(args[1]);
        boolean recursive = Arrays.asList(args).contains("--recursive");

        if (!path.toFile().isDirectory()) {
            System.err.println("Error: " + path + " is not a directory");
            return;
        }

        System.out.println("Starting to watch: " + path);
        if (recursive) {
            System.out.println("Recursive mode: enabled");
        }

        watcher = new DirectoryWatcher(index, TOKENIZER);

        try {
            watcher.watch(path, recursive);
            watcher.start();
            System.out.println("Watching for changes... (Press Ctrl+C to stop)");

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("\nStopping watcher...");
                    watcher.stop();
                }
            }));

            // Держим поток живым
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error watching directory: " + e.getMessage());
        }
    }

    private static void handleList() {
        List<IndexedFile> files = (List<IndexedFile>) index.getAllIndexedFiles();
        if (files.isEmpty()) {
            System.out.println("No files indexed yet.");
        } else {
            System.out.println("Indexed files (" + files.size() + "):");
            for (IndexedFile file : files) {
                System.out.println("  - " + file.getPath() + " (" + file.getSize() + " bytes)");
            }
        }
    }

    private static void handleClear() {
        index.clear();
        System.out.println("Index cleared.");
    }

    private static void printHelp() {
        System.out.println("Text Indexer - Command Line Interface\n");
        System.out.println("Usage: java -jar text-indexer.jar <command> [arguments]\n");
        System.out.println("Commands:");
        System.out.println("  --add <path> [--recursive]     Add file or directory to index");
        System.out.println("  --search <word>                Search for a word in indexed files");
        System.out.println("  --watch <path> [--recursive]   Watch directory for changes");
        System.out.println("  --list                         List all indexed files");
        System.out.println("  --clear                        Clear the index");
        System.out.println("  --help, -h                     Show this help message\n");
        System.out.println("Examples:");
        System.out.println("  java -jar text-indexer.jar --add /path/to/docs");
        System.out.println("  java -jar text-indexer.jar --search \"java\"");
        System.out.println("  java -jar text-indexer.jar --watch /path/to/docs --recursive");
    }
}