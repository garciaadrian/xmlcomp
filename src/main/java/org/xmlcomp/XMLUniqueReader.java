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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;


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
            throw new RuntimeException(e);
        }
    }

    public static String getNodeXPath(Node node) {
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            return "";
        }

        return getNodeXPath(node.getParentNode()) + "/" + node.getNodeName();
    }

    private boolean isCollaped() {
        return !nodes.isEmpty();
    }

    static private boolean areNodeEqual(Node n1, Node n2) {
        return true;
    }

    public boolean containsNode(Node node, String xPath) {
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


    public void diff(XMLUniqueReader doc2) {
        ArrayList<Node> matches = new ArrayList<>();
        for (Node node : nodes) {
            Node n = doc2.findNode(getNodeXPath(node), node);
            if (n == null) {
                System.out.println(" <<< MISMATCH <<<");
                System.out.println(nodeToString(node));
            } else {
                matches.add(n);
            }

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

    public Node findNode(String xPath, Node node) {
        for (Node n: nodes) {
            if (getNodeXPath(n).equals(xPath) && !isDiff(node, n)) {
                // found
                return n;
            }
        }

        return null;
    }

    public static boolean isAttribDiff(NamedNodeMap left, NamedNodeMap right) {
        if (left.getLength() != right.getLength()) {
            return true;
        }

        if (isAttribValueDiff(right, left)) return true;

        if (isAttribValueDiff(left, right)) return true;
        return false;
    }

    /**
     * Don't call this method directly. Call isAttribDiff instead which uses this method.
     * @param left
     * @param right
     * @return
     */
    private static boolean isAttribValueDiff(NamedNodeMap left, NamedNodeMap right) {
        for (int i = 0; i < right.getLength(); i++) {
            Node rightAttrib = right.item(i);
            Node leftAttrib = left.getNamedItem(rightAttrib.getNodeName());

            if (leftAttrib == null) {
                return true;
            }

            if (!rightAttrib.getNodeValue().equals(leftAttrib.getNodeValue())) {
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
