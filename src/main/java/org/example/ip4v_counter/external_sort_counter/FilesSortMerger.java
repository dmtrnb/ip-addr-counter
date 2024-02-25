package org.example.ip4v_counter.external_sort_counter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FilesSortMerger {

    private static final String TEMP_FILENAME = "tmp";

    public Path mergeAllFiles(Queue<Path> files, String filename) throws IOException {
        List<Path> stack = new LinkedList<>();

        while (!files.isEmpty()) {
            stack.add(files.remove());

            if (stack.size() >= 3) {
                long xSize = Files.size(stack.get(0));
                long zSize = Files.size(stack.get(2));
                if (xSize + Files.size(stack.get(1)) >= zSize) {
                    if (xSize <= zSize) {
                        mergeSortedFiles(stack, 0, 0);
                    } else {
                        mergeSortedFiles(stack, 2, 1);
                    }
                }
            }
            if (stack.size() >= 2 && Files.size(stack.get(0)) >= Files.size(stack.get(1))) {
                mergeSortedFiles(stack, 0, 0);
            }
        }

        Path newFile = Paths.get(filename);
        Files.deleteIfExists(newFile);
        Path file = stack.stream()
                .reduce(this::mergeTwoFiles)
                .orElse(newFile);
        Files.move(file, newFile);

        return newFile;
    }

    private void mergeSortedFiles(List<Path> stack, int toMerge, int toSet) {
        Path file = mergeTwoFiles(stack.get(toMerge), stack.get(1));
        stack.remove(1);
        stack.set(toSet, file);
    }

    private Path mergeTwoFiles(Path left, Path right) {
        try {
            Path tmp = Files.createFile(Paths.get(TEMP_FILENAME));

            sort(left, right, tmp);

            Files.delete(left);
            Files.delete(right);
            Files.move(tmp, left);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return left;
    }

    private void sort(Path fileLeft, Path fileRight, Path tempFile) {
        try (BufferedReader bufferedReaderA = Files.newBufferedReader(fileLeft);
             BufferedReader bufferedReaderB = Files.newBufferedReader(fileRight);
             BufferedWriter bufferedWriter = Files.newBufferedWriter(tempFile)
        ) {
            int compared;
            String left = null;
            String right = bufferedReaderB.readLine();

            while (left != null || right != null) {
                if (left == null && (left = bufferedReaderA.readLine()) == null) {
                    break;
                } else if (right == null && (right = bufferedReaderB.readLine()) == null) {
                    break;
                }

                compared = left.compareTo(right);

                if (compared <= 0) {
                    left = writeAndResetString(bufferedWriter, left);
                } else {
                    right = writeAndResetString(bufferedWriter, right);
                }
            }

            if (left != null) {
                writeStrings(bufferedReaderA, bufferedWriter, left);
            } else if (right != null) {
                writeStrings(bufferedReaderB, bufferedWriter, right);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeStrings(BufferedReader bufferedReader, BufferedWriter bufferedWriter, String str) throws IOException {
        bufferedWriter.write(str);
        bufferedWriter.newLine();
        while ((str = bufferedReader.readLine()) != null) {
            bufferedWriter.write(str);
            bufferedWriter.newLine();
        }
    }

    private String writeAndResetString(BufferedWriter bufferedWriter, String str) throws IOException {
        bufferedWriter.write(str);
        bufferedWriter.newLine();
        return null;
    }
}
