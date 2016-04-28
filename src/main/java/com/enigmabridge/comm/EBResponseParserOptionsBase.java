package com.enigmabridge.comm;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Basic options implementation.
 * Created by dusanklinec on 28.04.16.
 */
public class EBResponseParserOptionsBase implements EBResponseParserOptions {
    protected Map map;
    protected EBResponseParser parentParser;

    public void setParentParser(EBResponseParser parentParser) {
        this.parentParser = parentParser;
    }

    @Override
    public EBResponseParser getParentParser() {
        return parentParser;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Collection values() {
        return map.values();
    }

    @Override
    public Set<Entry> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
