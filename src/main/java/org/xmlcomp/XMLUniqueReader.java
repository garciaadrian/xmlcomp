/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

/**
 * XML document parser which assumes that elements in a document
 * have a unique key as an attribute
 *
 * Uses hashmaps to match nodes between the documents
 */
public class XMLUniqueReader implements XMLDiffGenerator {
    XMLUniqueReader(InputStream f) {
        _file = f;
    }

    private void collapseTree() {
        DocumentTraversal traversal = (DocumentTraversal) doc;
        NodeIterator it = traversal.createNodeIterator(doc.getDocumentElement(),
        NodeFilter.SHOW_ELEMENT, null, true);

        for (Node node = it.nextNode(); node != null; node = it.nextNode()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attributes = node.getAttributes();
                String nodeId = attributes.getNamedItem("id").getNodeValue();
                String nodeName = node.getNodeName();
                String uniqueId = nodeId.concat(nodeName);
                hashedNodes.put(uniqueId, node);
                nodes.add(node);
            }
        }
    }

    private boolean isCollaped() {
        return !nodes.isEmpty();
    }

    public boolean containsNode(Node node) {
        for (Node n : nodes) {
            if (n.isEqualNode(node)) {
                return true;
            }
        }
        return false;
    }

    public Node findNode(String element) {
        for (Node node : nodes) {
            if (node.getNodeName().equals(element)) {
                return node;
            }
        }

        return null;
    }

    public static void diffDocs(XMLUniqueReader leftDoc, XMLUniqueReader rightDoc) {

    }

    public void diff(XMLUniqueReader doc2) {
        for (Node node : nodes) {

        }
    }

    public Node findNode(String element, String key, String val) {
        for (Node node : nodes) {
            if (node.getNodeName().equals(element)) {
                NamedNodeMap attributes = node.getAttributes();
                Node attrib = attributes.getNamedItem(key);
                if (attrib == null) {
                    continue;
                }

                if (attrib.getNodeValue().equals(val)) {
                    return node;
                }
            }
        }

        return null;
    }

    public static boolean isAttribDiff(NamedNodeMap left, NamedNodeMap right) {
        if (left.getLength() != right.getLength()) {
            return true;
        }

        for (int i = 0; i < left.getLength(); i++) {
            Node leftAttrib = left.item(i);
            /*
             What if a node has two attribute nodes with the same name?
             what will the DOM api return?
            */
            Node rightAttrib = right.getNamedItem(leftAttrib.getNodeName());

            if (rightAttrib == null) {
                return true;
            }

            if (!leftAttrib.getNodeValue().equals(rightAttrib.getNodeValue())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isDiff(Node left, Node right) {
        // For now, let's assume the node 'right' is a root node
        NamedNodeMap leftAttributes = left.getAttributes();
        NamedNodeMap rightAttributes = right.getAttributes();

        if (!isAttribDiff(leftAttributes, rightAttributes) &&
        left.getNodeName().equals(right.getNodeName())) {
            return false;
        }
        return true;
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
            doc = db.parse(_file);
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        doc.normalizeDocument();

        if (doc.hasChildNodes()) {
            collapseTree();
        }

    }

    @Override
    public String getDiff() {
        return null;
    }

    InputStream _file = null;
    Document doc = null;
    ArrayList<Node> nodes = new ArrayList<>();
    HashMap<String, Node> hashedNodes = new HashMap<>();
}
