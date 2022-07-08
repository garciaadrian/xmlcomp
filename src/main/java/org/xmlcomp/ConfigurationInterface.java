package org.xmlcomp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ConfigurationInterface {
    public String getProperty(String property);
    public ArrayList<Key> getKey();
    static public class Key {
        public String element;
        public List<Map<String, String>> attributes;
    }
}
