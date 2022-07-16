/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements XmlParser interface.
 * This implementation relies on the fact that each node in the source & target documents
 * has at least one attribute which can be used as a unique key in a hash map, allowing
 * O(n) performance when comparing both documents.
 *
 * Uses Java's DOM parser to load both documents into memory
 */
public class XmlUniqueParser implements XmlParser {
    private static final Logger logger = LogManager.getLogger();
    private XmlUniqueParser(){}

    /**
     * Instantiates a new XmlUniqueParser.
     *
     * @param config the config
     */
    public XmlUniqueParser(ConfigurationInterface config) {
        this.config = config;

        sourceDocStream = openResource(config.getProperty("source"));
        targetDocStream = openResource(config.getProperty("target"));

        sourceDocument = loadDocument(sourceDocStream);
        targetDocument = loadDocument(targetDocStream);

        sourceDocNodeHashes = loadNodes(sourceDocument);
        targetDocNodeHashes = loadNodes(targetDocument);
        HashMap<String, Node> unmatchedNodes = findUnmatchedNodes();
    }

    public XmlUniqueParser(String source, String target) {
        sourceDocStream = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
        targetDocStream = new ByteArrayInputStream(target.getBytes(StandardCharsets.UTF_8));

        sourceDocument = loadDocument(sourceDocStream);
        targetDocument = loadDocument(targetDocStream);

        sourceDocNodeHashes = loadNodes(sourceDocument);
        targetDocNodeHashes = loadNodes(targetDocument);
        HashMap<String, Node> unmatchedNodes = findUnmatchedNodes();
    }

    /**
     * Logs the list of unmatched nodes
     *
     * @param unmatchedNodes the hashmap of unmatched nodes
     */
    public void getDiff( HashMap<String, Node> unmatchedNodes) {
        for (Map.Entry<String, Node> entry : unmatchedNodes.entrySet()) {
            String node = nodeToString(entry.getValue());
            logger.warn("--- Node MISMATCH --- {}", node);
        }
    }

    /**
     * Finds nodes that don't exist in both source & target document node hashmaps.
     *
     * @return the hash map of unmatched nodes
     */
    public HashMap<String, Node> findUnmatchedNodes() {
        HashMap<String, Node> unmatchedNodes = new HashMap<>();
        for (Map.Entry<String, Node> entry : sourceDocNodeHashes.entrySet()) {
            Node targetEntry = targetDocNodeHashes.getOrDefault(entry.getKey(), null);
            if (targetEntry == null) {
                unmatchedNodes.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Node> entry : targetDocNodeHashes.entrySet()) {
            Node sourceEntry = sourceDocNodeHashes.getOrDefault(entry.getKey(), null);
            if (sourceEntry == null) {
                unmatchedNodes.put(entry.getKey(), entry.getValue());
            }
        }

        return unmatchedNodes;
    }

    /**
     * Iterates through the documents nodes and creates a corresponding entry in a hash map.
     * Each node of the document must have at least one attribute which can be used as a unique key
     *
     * @param doc the document
     * @return the hash map
     */
    public HashMap<String, Node> loadNodes(Document doc) {
        HashMap<String, Node> nodes = new HashMap<>();

        DocumentTraversal traversal = (DocumentTraversal) doc;
        NodeIterator it =  traversal.createNodeIterator(doc.getDocumentElement(),
                NodeFilter.SHOW_ELEMENT, null, true);

        // #TODO(adrian): this key stuff looks messy. change interface for retrieving key
        // #TODO(adrian): only allow 1 key entry?
        String uniqueKey = "id";

        for (Node node = it.nextNode(); node != null; node = it.nextNode()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = node.getAttributes();
                Node idAttrib = attributes.getNamedItem(uniqueKey);
                if (idAttrib != null)
                    nodes.put(idAttrib.getTextContent(), node);
            }
        }

        return nodes;
    }

    /**
     * Loads an XML doc into memory using Java's DOM api.
     * Must be called before loadNodes()
     *
     * @param file the file
     * @return the document
     */
    public Document loadDocument(InputStream file) {
        Document doc = null;
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        try {
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.fatal("Unable to parse document {}", file);
            System.exit(1);
        }

        try {
            doc = db.parse(file);
        } catch (IOException | SAXException e) {
            logger.fatal("Unable to parse document {}", file);
            System.exit(1);
        }

        doc.normalizeDocument();
        return doc;
    }

    /**
     * Returns the xml representation of a node.
     *
     * @param node the node
     * @return xml
     */
    public static String nodeToString(Node node) {
        try {
            node.normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("//text()[normalize-space()='']");
            NodeList nodeList = (NodeList) expr.evaluate(node, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nd = nodeList.item(i);
                nd.getParentNode().removeChild(nd);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (XPathExpressionException | TransformerException e) {
            logger.fatal("Unable to generate XPath for node");
            System.exit(1);
        }
        return "";
    }

    /**
     * The app config.
     */
    ConfigurationInterface config;
    /**
     * The Source document input stream.
     */
    InputStream sourceDocStream = null;
    /**
     * The Target document input stream.
     */
    InputStream targetDocStream = null;
    /**
     * The Source document.
     */
    Document sourceDocument = null;
    /**
     * The Target document.
     */
    Document targetDocument = null;

    /**
     * The Source doc node hashes.
     */
    HashMap<String, Node> sourceDocNodeHashes;
    /**
     * The Target doc node hashes.
     */
    HashMap<String, Node> targetDocNodeHashes;
}
