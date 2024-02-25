package org.example.ip4v_counter.external_sort_counter;

import org.example.ip4v_counter.counter.Ip4vAddrCounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExternalSortIp4vAddrCounter implements Ip4vAddrCounter {

    private final ExternalSorter sorter;

    public ExternalSortIp4vAddrCounter() {
        this.sorter = new ExternalSorter();
    }

    @Override
    public long count(String filename) {
        Path file = sorter.sort(filename);
        return countResultFile(file);
    }

    private long countResultFile(Path file) {
        long count = 0L;
        try (BufferedReader bufferedReader = Files.newBufferedReader(file)) {
            String last = bufferedReader.readLine();
            if (last == null) {
                return count;
            }

            count++;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals(last)) {
                    continue;
                }
                last = line;
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return count;
    }
}
