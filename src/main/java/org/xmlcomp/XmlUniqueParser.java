/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * We assume that each node in the hierarchy has an attribute which
 * can be used as a unique key when we match nodes from each tree
 */
public class XmlUniqueParser implements XmlParser {
    private XmlUniqueParser(){}

    public XmlUniqueParser(ConfigurationInterface config) {
        this.config = config;
        String firstSource = config.getProperty("firstSource");
        String secondSource = config.getProperty("secondSource");
    }

    public void read() {
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        try {
            doc = db.parse(file);
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        doc.normalizeDocument();

        if (doc.hasChildNodes()) {
        }
    }

    ConfigurationInterface config;
    InputStream file = null;
    Document doc = null;
}
