/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommonCsvParserTest {
    private CommonCsvParser parser;

    @BeforeEach
    void setUp() {
        String source = "Year,Industry_aggregation_NZSIOC,Industry_code_NZSIOC,Industry_name_NZSIOC,Units\n" +
                "2021,Level 1,99999,All industries,Dollars (millions)\n" +
                "2021,Level 1,99999,All industries,Dollars (millions)\n";

        String target = "Year,Industry_aggregation_NZSIOC,Industry_code_NZSIOC,Industry_name_NZSIOC,Units\n" +
                "2021,Level 1,99999,All industries,Dollars (millions)\n" +
                "2021,Level 1,11111,All industries,Dollars (millions)\n";
        parser = new CommonCsvParser(source, target);
    }

    @Test
    void listDifferences() {
        List<String> differences = parser.listDifferences();
        assertEquals(2, differences.size());
    }
}