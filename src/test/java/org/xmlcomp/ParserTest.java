/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private Parser parser;

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
    void openResourceNull() {
        ClassLoader loader = getClass().getClassLoader();
        URL file = loader.getResource("test.csv");
        InputStream stream = null;
        try {
            stream = parser.openResource("test.csv");
        } catch (NullPointerException e) {

        } finally {
            assertNull(stream);
        }

    }

    @Test
    void openNull() {
        InputStream stream = null;
        try {
            stream = parser.open("/etc/passwd/123fakefile/fakefake/123/etc/1");
        } catch (FileNotFoundException e) {

        } finally {
            assertNull(stream);
        }
    }
}