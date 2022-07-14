/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XmlUniqueParserTest {
    private XmlUniqueParser parser = null;

    @Test
    void testNestedNodeToString() {
        String xml = XmlUniqueParser.nodeToString(parser.sourceDocNodeHashes.get("17546"));
        String originalXml = "<library name=\"Hardstyle\" id=\"17546\">";
        assert(xml.equals(originalXml));

    }

    @Test
    void testLeafNodeToString() {
        String xml = XmlUniqueParser.nodeToString(parser.sourceDocNodeHashes.get("99836"));
        // #TODO(adrian): DOM rearranges attribute order alphabetically. This may not be desirable
        String originalXml = "<song artist=\"James Charles\" id=\"99836\" title=\"Hi sisters\"/>\r\n";
        assert(xml.equals(originalXml));
    }

    @BeforeEach
    void setUp() {
        String sourceXml = "<library name=\"Hardstyle\" id=\"17546\">\n" +
                "    <album name=\"Thunderdome 1997\" id=\"03572\">\n" +
                "        <song title=\"Raveworld\" artist=\"Distortion &amp; MC Raw &amp; Bass D\" id=\"23853\"/>\n" +
                "        <song title=\"Mosquito\" artist=\"Maurizio Braccagni\" id=\"01923\"/>\n" +
                "        <song title=\"We're Gonna Blow Your Mind\" artist=\"Rotterdam Terror Corps\" id=\"13456\"/>\n" +
                "        <song title=\"Hi sisters\" artist=\"James Charles\" id=\"99836\"/>\n" +
                "    </album>\n" +
                "    <album name=\"Nepotist\" id=\"00918\">\n" +
                "        <song title=\"Juicy\" artist=\"Illenium\" id=\"45728\"/>\n" +
                "        <song title=\"Glizzy\" artist=\"Hydrocoque\" id=\"23133\"/>\n" +
                "        <song title=\"Jizzy\" artist=\"Hydrocoque &amp; XXXayene\" id=\"99834\"/>\n" +
                "        <song title=\"Hi sisters\" artist=\"James Charles\" id=\"99831\"/>\n" +
                "    </album>\n" +
                "    <song title=\"Hi sisters\" artist=\"James Charles\" id=\"00234\"/>\n" +
                "    <album/>\n" +
                "</library>";

        String targetXml = "<library name=\"Hardstyle\" id=\"17546\">\n" +
                "    <album name=\"Thunderdome 1997\" id=\"03572\">\n" +
                "        <song title=\"Raveworld\" artist=\"Distortion &amp; MC Raw &amp; Bass D\" id=\"23853\"/>\n" +
                "        <song title=\"Mosquito\" artist=\"Maurizio Braccagni\" id=\"01923\"/>\n" +
                "        <song title=\"We're Gonna Blow Your Mind\" artist=\"Rotterdam Terror Corps\" id=\"13456\"/>\n" +
                "    </album>\n" +
                "    <album name=\"Nepotistz\" id=\"00912\">\n" +
                "        <song title=\"Juicy\" artist=\"Illenium\" id=\"45728\"/>\n" +
                "        <song title=\"Glizzy\" artist=\"Hydrocoque\" id=\"23133\"/>\n" +
                "        <song title=\"I am Ruler\" artist=\"DJ Anime\" id=\"23130\"/>\n" +
                "    </album>\n" +
                "</library>";

        parser = new XmlUniqueParser(sourceXml, targetXml);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testNodeToString() {
    }
}