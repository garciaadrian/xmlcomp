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

public class CommonCsvParser implements Parser {
    private CommonCsvParser() {}

    private static final Logger logger = LogManager.getLogger();

    public CommonCsvParser(ConfigurationInterface config) {
        this.config = config;

        sourceDocStream = openResource(config.getProperty("source"));
        targetDocStream = openResource(config.getProperty("target"));

        Iterable<CSVRecord> sourceRecords = getCsvRecordsIterable(sourceDocStream);
        constructCsvEntryHashTable(sourceRecords);
    }

    private void constructCsvEntryHashTable(Iterable<CSVRecord> records) {
        for (CSVRecord record : records) {
            byte[] entryBytes = record.toString().getBytes();
            int digest =
                    org.apache.commons.codec.digest.MurmurHash3.hash32x86(entryBytes, 0, entryBytes.length, 0);
        }
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
