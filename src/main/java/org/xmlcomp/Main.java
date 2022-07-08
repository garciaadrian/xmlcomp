package org.xmlcomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.net.URL;

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

        System.out.println(config.getProperty("firstSource"));

        XmlUniqueReader local_xml = new XmlUniqueReader(Main.class.getResourceAsStream("/uwm.xml"));
        local_xml.read();

        XmlUniqueReader internet_xml = new XmlUniqueReader(Main.class.getResourceAsStream("/uwm_new.xml"));
        internet_xml.read();

        long curr = System.currentTimeMillis();
        local_xml.diff(internet_xml);
        System.out.println("DIFF BENCHMARK: " + (System.currentTimeMillis() - curr));



        XmlParserFactory pf = new XmlParserFactory();
        XmlParser xml = pf.createParser(config);

    }
}