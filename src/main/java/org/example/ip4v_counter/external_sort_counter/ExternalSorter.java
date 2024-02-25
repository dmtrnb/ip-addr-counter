package org.example.ip4v_counter.external_sort_counter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class ExternalSorter {

    private static final int AVG_IP_ADDR_SIZE = 64;
    private static final int MAX_COUNT_OF_FILES = 1024;
    private static final String RESULT_FILENAME = "result";
    private static final String SORTED_FILENAME = "sortedFile";

    private final int maxStrings;
    private final FilesSortMerger merger;
    private final List<Queue<Path>> sortedFileList;

    public ExternalSorter() {
        Runtime runtime = Runtime.getRuntime();
        long memory = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
        this.maxStrings = (int) (memory / AVG_IP_ADDR_SIZE / 2);
        this.merger = new FilesSortMerger();
        this.sortedFileList = new ArrayList<>(4);
        this.sortedFileList.add(new LinkedList<>());
    }

    public Path sort(String filename) {
        Path result;

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8)) {
            int index = 0;
            String line;
            String[] strings = new String[maxStrings];
            while ((line = bufferedReader.readLine()) != null) {
                strings[index] = line;
                index++;
                if (index == maxStrings) {
                    sortedFileList.get(0).add(sortWriteAndClear(strings));
                    index = 0;

                    if (sortedFileList.get(0).size() == MAX_COUNT_OF_FILES) {
                        merge();
                    }
                }
            }

            if (index != 0) {
                Path sortedFile = sortWriteAndClear(Arrays.copyOf(strings, index));
                sortedFileList.get(0).add(sortedFile);
            }

            result = mergeAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private void merge() throws IOException {
        for (int i = 0; i < sortedFileList.size(); i++) {
            Queue<Path> sortedFiles = sortedFileList.get(i);
            if (sortedFiles.size() == MAX_COUNT_OF_FILES) {
                int nextQueueIndex = i + 1;
                if (nextQueueIndex == sortedFileList.size()) {
                    sortedFileList.add(new LinkedList<>());
                }
                int nextQueueSize = sortedFileList.get(nextQueueIndex).size();
                String newFilename = SORTED_FILENAME + "0".repeat(i + 1) + nextQueueSize;
                Path mergedFile = merger.mergeAllFiles(sortedFiles, newFilename);
                sortedFiles.clear();
                sortedFileList.get(nextQueueIndex).add(mergedFile);
            } else {
                break;
            }
        }
    }

    private Path mergeAll() throws IOException {
        Queue<Path> sortedFiles = sortedFileList.stream()
                .flatMap(Queue::stream)
                .collect(Collectors.toCollection(LinkedList::new));

        return merger.mergeAllFiles(sortedFiles, RESULT_FILENAME);
    }

    private Path sortWriteAndClear(String[] strings) throws IOException {
        Arrays.sort(strings);

        String filename = SORTED_FILENAME + sortedFileList.get(0).size();
        Path path = Paths.get(filename);
        Files.deleteIfExists(path);
        Path file = Files.createFile(path);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file)) {
            for (String str: strings) {
                bufferedWriter.write(str);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }
}
