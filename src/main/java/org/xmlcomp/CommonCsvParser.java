/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonCsvParser implements Parser {
    private CommonCsvParser() {}

    public CommonCsvParser(ConfigurationInterface config) {
        this.config = config;
    }

    public CommonCsvParser(String source, String target) {
        try {
            sourceDocStream = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
            targetDocStream = new ByteArrayInputStream(target.getBytes(StandardCharsets.UTF_8));
        } catch (NullPointerException e) {
            logger.warn(e.getMessage());
            throw e;
        }

    }

    /**
     * > It takes two hashmaps source and entry which are hashmaps of a CSVRecord entry and its hash,
     * and returns a list of CSVRecord's as a string, that are missing from the source hashmap.
     * In order to find entries that are mutually exclusive from two hashmaps, call this function
     * twice swapping the order of arguments.
     *
     * @param sourceEntry The source entry that we're comparing against.
     * @param targetEntry The target entry that we're comparing against.
     * @return A list of missing entries from the source hashmap.
     */
    private List<String> listMissingEntries(HashMap<Integer, CSVRecord> sourceEntry,
                                            HashMap<Integer, CSVRecord> targetEntry) {
        List<String> missingEntries = new ArrayList<>();

        targetEntry.forEach((hash, entry) -> {
            if (!sourceEntry.containsKey(hash)) {
                missingEntries.add(entry.toString());
            }
        });

        return missingEntries;
    }
    public List<String> listDifferences() {
        if (config != null)
            loadConfigResources();

        HashMap<Integer, CSVRecord> sourceEntryHashMap =
                getCsvEntriesHashTable(getCsvEntriesIterable(sourceDocStream));
        HashMap<Integer, CSVRecord> targetEntryHashMap =
                getCsvEntriesHashTable(getCsvEntriesIterable(targetDocStream));

        List<String> sourceMissingEntries = listMissingEntries(sourceEntryHashMap, targetEntryHashMap);
        List<String> targetMissingEntries = listMissingEntries(targetEntryHashMap, sourceEntryHashMap);


        List<String> differences = new ArrayList<>();
        differences.addAll(sourceMissingEntries);
        differences.addAll(targetMissingEntries);

        return differences;
    }

    /**
     * If the config object is not null, then opens the source and target documents.
     * @throws NullPointerException if resources do not exist
     */
    private void loadConfigResources() {
        if (config != null) {
            sourceDocStream = openResource(config.getProperty("source"));
            targetDocStream = openResource(config.getProperty("target"));
        }
    }

    /**
     * Takes an iterable of CSVRecord entries and then creates a hash map, hashing the value
     * of the csv entry (the value of a csv entry is the entire row as a string), which
     * maps to the CSVRecord object.
     *
     * The hash value of the csv entry can then be used to identify unique csv records in
     * a document.
     *
     * @param records the CSV records to be hashed
     * @return A HashMap of CSVRecords.
     */
    private HashMap<Integer, CSVRecord> getCsvEntriesHashTable(Iterable<CSVRecord> records) {
        HashMap<Integer, CSVRecord> hashes = new HashMap<>();
        for (CSVRecord entry : records) {
            byte[] entryBytes = entry.toString().getBytes();
            int digest =
                    org.apache.commons.codec.digest.MurmurHash3
                            .hash32x86(entryBytes, 0, entryBytes.length, 0);
            hashes.put(digest, entry);
        }

        return hashes;
    }

    /**
     * > This function takes an input stream and returns an iterable of CSV entries/records
     *
     * @param stream The input stream of the CSV file
     * @return An Iterable of CSVRecord objects.
     */
    private Iterable<CSVRecord> getCsvEntriesIterable(InputStream stream) {
        Reader sourceReader = new BufferedReader(new InputStreamReader(stream));
        Iterable<CSVRecord> records = null;
        try {

            CSVFormat builder = CSVFormat.Builder.create().build();
            records = builder.parse(sourceReader);
        } catch (IOException e) {
            logger.fatal("Unable to parse CSV file {}", e.getMessage());
            System.exit(1);
        }

        return records;
    }

    private ConfigurationInterface config = null;
    private InputStream sourceDocStream = null;
    private InputStream targetDocStream = null;
}
