package org.xmlcomp;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class YAMLConfiguration implements ConfigurationInterface{
    public Map<String, String> sources;
    public ArrayList<Key> keys;

    @JsonAnyGetter
    private Map<String, String> getSources() {
        return sources;
    }

    @JsonAnySetter
    private void setSources(Map<String, String> properties) {
        this.sources = properties;
    }

    public String getProperty(String property) {
        return sources.getOrDefault(property, "");
    }

    static YAMLConfiguration getConfig(URL file) throws IOException {
        ObjectMapper mapper = new YAMLMapper(new YAMLFactory());
        return mapper.readValue(file, YAMLConfiguration.class);
    }

    @Override
    public ArrayList<Key> getKey() {
        return keys;
    }
}
