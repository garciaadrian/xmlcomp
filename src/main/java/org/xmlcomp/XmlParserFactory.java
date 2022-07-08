/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.xmlcomp;

public class XmlParserFactory implements ParserFactory {
    /**
     * @param config YAMLConfiguration config file
     * @return XmlParser based on config settings
     */
    @Override
    public XmlParser createParser(ConfigurationInterface config) {
        // Here you would pick a different implementation of an XmlParser depending on config
        // For now let's pick XmlUniqueParser as a default.
        return new XmlUniqueParser(config);
    }
}
