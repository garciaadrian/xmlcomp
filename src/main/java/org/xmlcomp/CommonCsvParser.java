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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;

public class CommonCsvParser implements Parser {
    private CommonCsvParser() {}

    private static final Logger logger = LogManager.getLogger();

    public CommonCsvParser(ConfigurationInterface config) {
        this.config = config;
    }

    public void Diff() {
        long currMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        try {
            loadConfigResources();
        } catch (NullPointerException e) {
            logger.warn("Unable to load resources {}, or {}", config.getProperty("source"),
                    config.getProperty("target"));
            throw e;
        }

        long curr = System.currentTimeMillis();

        HashMap<Integer, CSVRecord> sourceEntryHashMap =
                constructCsvEntryHashTable(getCsvRecordsIterable(sourceDocStream));
        HashMap<Integer, CSVRecord> targetEntryHashMap =
                constructCsvEntryHashTable(getCsvRecordsIterable(targetDocStream));

        HashMap<Integer, CSVRecord> unmatchedEntries =
                listOfUnmatchedEntries(sourceEntryHashMap, targetEntryHashMap);

        logUnmatchedEntries(unmatchedEntries);

        int mb = 1024 * 1024;
        long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long end = System.currentTimeMillis();
        logger.warn("Took {} ms to diff {} entries",
                (end - curr), sourceEntryHashMap.size() + targetEntryHashMap.size());
        logger.warn("Used {} megabytes of memory", (endMem - currMem) / mb);
    }

    private InputStream getResourceAsStream(String path) {
        InputStream stream;

        try {
            stream = openResource(path);
        } catch (NullPointerException e) {
            logger.warn("File may not exist: {}", path);
            throw e;
        }

        if (stream == null) {
            throw new NullPointerException();
        }
        return stream;
    }
    public void loadConfigResources() {
        try {
            sourceDocStream = getResourceAsStream(config.getProperty("source"));
            targetDocStream = getResourceAsStream(config.getProperty("target"));
        } catch (NullPointerException e) {
            throw e;
        }
    }

    private void logUnmatchedEntries(HashMap<Integer, CSVRecord> entries) {
        entries.forEach((hash, entry) -> {
            logger.warn("Unmatched Entry: {}", entry);
        });
    }

    private HashMap<Integer, CSVRecord> listOfUnmatchedEntries(HashMap<Integer, CSVRecord> source,
                                                               HashMap<Integer, CSVRecord> target) {
        HashMap<Integer, CSVRecord> unmatchedEntries = new HashMap<>();
        source.forEach(((hash, entry) -> {
            if (!target.containsKey(hash)) {
                unmatchedEntries.put(hash, entry);
            }
        }));

        target.forEach((hash, entry) -> {
            if (!source.containsKey(hash)) {
                unmatchedEntries.put(hash, entry);
            }
        });

        return unmatchedEntries;
    }
    private HashMap<Integer, CSVRecord> constructCsvEntryHashTable(Iterable<CSVRecord> records) {
        HashMap<Integer, CSVRecord> hashes = new HashMap<>();
        for (CSVRecord record : records) {
            byte[] entryBytes = record.toString().getBytes();
            int digest =
                    org.apache.commons.codec.digest.MurmurHash3.hash32x86(entryBytes, 0, entryBytes.length, 0);
            hashes.put(digest, record);
        }

        return hashes;
    }

    private Iterable<CSVRecord> getCsvRecordsIterable(InputStream stream) {
        Reader sourceReader = new BufferedReader(new InputStreamReader(stream));
        Iterable<CSVRecord> records = null;
        try {

            CSVFormat builder = CSVFormat.Builder.create().setDelimiter(';').build();
            records = builder.parse(sourceReader);
        } catch (IOException e) {
            logger.fatal("Unable to parse CSV file {}", e.getMessage());
            System.exit(1);
        }

        return records;
    }

    private ConfigurationInterface config = null;
    private InputStream sourceDocStream;
    private InputStream targetDocStream;
}
