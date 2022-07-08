package org.xmlcomp;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class YAMLConfiguration implements ConfigurationInterface{
    private Map<String, String> sources;
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

    @Override
    public ArrayList<Key> getKey() {
        return keys;
    }
}
