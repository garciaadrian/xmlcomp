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

        long curr = System.currentTimeMillis();
        sourceDocStream = openResource(config.getProperty("source"));
        targetDocStream = openResource(config.getProperty("target"));

        sourceEntryHashMap = constructCsvEntryHashTable(getCsvRecordsIterable(sourceDocStream));
        targetEntryHashMap = constructCsvEntryHashTable(getCsvRecordsIterable(targetDocStream));

        HashMap<Integer, CSVRecord> unmatchedEntries =
                listOfUnmatchedEntries(sourceEntryHashMap, targetEntryHashMap);
        logUnmatchedEntries(unmatchedEntries);
        long end = System.currentTimeMillis();
        logger.warn("Took {} ms to diff 2 csv files", (end - curr));
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
    private HashMap<Integer, CSVRecord> sourceEntryHashMap = new HashMap<>();
    private HashMap<Integer, CSVRecord> targetEntryHashMap = new HashMap<>();
}
