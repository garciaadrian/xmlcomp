/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

/**
 * Implements X-tree Diff+ Change Detection Algorithm
 * A model that represents changes in a hierarchical structure only
 * using parent relationships
 */
public class XmlTreeDiff implements XmlDiff {
    private XmlTreeDiff() {};

    public static class XTree {
        String label;
        String type;
        String value;
        int index;
        String iMD;
        String nMD;
        String tMD;
    }


    @Override
    public String getDiff() {
        return null;
    }
}
