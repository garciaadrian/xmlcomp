package org.xmlcomp;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class YAMLConfiguration implements ConfigurationInterface{
    static public class Key {
        public String element;
        public List<Map<String, String>> attributes;
    }

    private Map<String, String> input;
    public String output;
    public ArrayList<Key> keys;

    @JsonAnyGetter
    private Map<String, String> getInput() {
        return input;
    }

    @JsonAnySetter
    private void setInput(Map<String, String> properties) {
        this.input = properties;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public String getProperty(String property) {
        return input.getOrDefault(property, "");
    }

}
