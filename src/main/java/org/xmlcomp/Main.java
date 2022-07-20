package org.xmlcomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        CommonCsvParser csvParser = new CommonCsvParser(config);
        try {
            List<String> diff = csvParser.listDifferences();
            diff.forEach(logger::warn);
        } catch (NullPointerException e) {
            logger.fatal("Exiting program...");
            return;
        }
    }
}