package org.xmlcomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        ObjectMapper mapper = new YAMLMapper(new YAMLFactory());

        URL options = Main.class.getResource("/options.yaml");

        YAMLConfiguration config;

        try {
            config = mapper.readValue(options, YAMLConfiguration.class);
        } catch (IOException e) {
            logger.fatal("Could not open configuration file.");
            return;
        }

        logger.debug(config.getProperty("source"));



        //XmlParserFactory pf = new XmlParserFactory();
        //XmlParser xml = pf.createParser(config);

        Parser csvParser = new CommonCsvParser(config);
    }
}