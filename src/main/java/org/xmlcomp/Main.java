package org.xmlcomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ObjectMapper mapper = new YAMLMapper(new YAMLFactory());

        URL options = Main.class.getResource("/options.yaml");

        YAMLConfiguration config;

        try {
            config = mapper.readValue(options, YAMLConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println(config.getProperty("source_1"));

        XMLUniqueReader local_xml = new XMLUniqueReader(Main.class.getResourceAsStream("/local_library.xml"));
        local_xml.read();

        XMLUniqueReader internet_xml = new XMLUniqueReader(Main.class.getResourceAsStream("/internet_library.xml"));
        internet_xml.read();

        Node localRootNode = local_xml.findNode("library");
        Node internetRootNode = internet_xml.findNode("library");
        XMLUniqueReader.isAttribDiff(localRootNode.getAttributes(), internetRootNode.getAttributes());
        local_xml.diff(internet_xml);

    }
}