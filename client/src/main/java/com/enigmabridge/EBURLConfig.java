package com.enigmabridge;

import com.enigmabridge.comm.EBConnectionSettings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Represents one-string configuration of EB engine / UO.
 * Example: https://site2.enigmabridge.com:11180/?apiKey=API_TEST
 *
 * Created by dusanklinec on 05.07.16.
 */
public class EBURLConfig implements EBSettings {
    private static final String FIELD_API_KEY = "apiKey";
    private static final String FIELD_SETTINGS = "settings";

    /**
     * API key for using EB service.
     */
    protected String apiKey;

    /**
     * Connection string to the EB endpoint
     * https://site1.enigmabridge.com:11180
     */
    protected EBEndpointInfo endpointInfo = new EBEndpointInfo("https://site1.enigmabridge.com:11180");

    /**
     * Connection settings for UO operation.
     */
    protected EBConnectionSettings connectionSettings;

    /**
     * Root of the json parsed settings.
     */
    protected final JSONObject jsonRoot = new JSONObject();

    public EBURLConfig() throws MalformedURLException {
    }

    public static abstract class AbstractBuilder<T extends EBURLConfig, B extends EBURLConfig.AbstractBuilder> {
        public B setApiKey(String apiKey) {
            getObj().setApiKey(apiKey);
            return getThisBuilder();
        }

        public B setEndpointInfo(EBEndpointInfo endpointInfo) {
            getObj().setEndpointInfo(endpointInfo.copy());
            return getThisBuilder();
        }

        public B setConnectionSettings(EBConnectionSettings connectionSettings) {
            getObj().setConnectionSettings(connectionSettings.copy());
            return getThisBuilder();
        }

        public B addElement(EBJSONSerializable ebjsonSerializable, String key){
            getObj().addElement(ebjsonSerializable, key);
            return getThisBuilder();
        }

        public B setURLConfig(String config) throws MalformedURLException, UnsupportedEncodingException {
            getObj().fromUrl(config);
            return getThisBuilder();
        }

        public B setFromSettings(EBSettings settings) {
            if (settings != null){
                getObj().setApiKey(settings.getApiKey());
                getObj().setConnectionSettings(settings.getConnectionSettings());
                getObj().setEndpointInfo(settings.getEndpointInfo());
            }
            return getThisBuilder();
        }

        public B setFromSettingsIfNotSet(EBSettings settings) {
            if (settings != null){
                if (getObj().getApiKey() == null){
                    getObj().setApiKey(settings.getApiKey());
                }

                if (getObj().getConnectionSettings() == null) {
                    getObj().setConnectionSettings(settings.getConnectionSettings());
                }

                if (getObj().getEndpointInfo() == null) {
                    getObj().setEndpointInfo(settings.getEndpointInfo());
                }
            }
            return getThisBuilder();
        }

        public B setFromEngine(EBEngine engine) {
            final EBSettings defs = engine.getDefaultSettings();
            return setFromSettings(defs);
        }

        public B setFromEngineIfNotSet(EBEngine engine) {
            final EBSettings defs = engine.getDefaultSettings();
            return setFromSettingsIfNotSet(defs);
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBURLConfig, EBURLConfig.Builder> {
        private final EBURLConfig parent = new EBURLConfig();

        public Builder() throws MalformedURLException {
        }

        @Override
        public EBURLConfig.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBURLConfig getObj() {
            return parent;
        }

        @Override
        public EBURLConfig build() {
            return parent;
        }
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public EBEndpointInfo getEndpointInfo() {
        return endpointInfo;
    }

    @Override
    public EBConnectionSettings getConnectionSettings() {
        return connectionSettings;
    }

    protected void fromUrl(String url) throws MalformedURLException, UnsupportedEncodingException {
        this.endpointInfo = new EBEndpointInfo(url);

        final URL urlObj = new URL(url);
        final String queryString = urlObj.getQuery();

        final JSONObject root = deserializeData(queryString);
        if (root == null){
            return;
        }

        // Api key?
        final String tmpApiKey = root.has(FIELD_API_KEY) ?
                    root.getString(FIELD_API_KEY) : null;


        // Settings.
        final EBConnectionSettings tmpConnSettings = root.has(FIELD_SETTINGS) ?
                new EBConnectionSettings(root.getJSONObject(FIELD_SETTINGS)) : null;

        // Apply, no exception so far.
        if (tmpApiKey != null){
            this.apiKey = tmpApiKey;
        }
        if (tmpConnSettings != null) {
            this.connectionSettings = tmpConnSettings;
        }
        EBUtils.mergeInto(this.jsonRoot, root);
    }

    /**
     * Serializes object under given key to the settings. Can be retrieved with getElement().
     *
     * @param ebjsonSerializable
     * @param key, if contains dot, considered as path, descends.
     */
    protected void addElement(EBJSONSerializable ebjsonSerializable, String key){
        final String[] path = key.split("\\.");
        final String last = path[path.length-1];
        final JSONObject parent = getParentNode(jsonRoot, path, true);
        parent.put(last, ebjsonSerializable.toJSON(null));
    }

    /**
     * Returns URL configuration representing current settings in this object.
     * @return url configuration
     */
    public String toString(){
        final JSONObject mainRoot = jsonRoot;
        if (this.apiKey != null){
            mainRoot.put(FIELD_API_KEY, this.apiKey);
        }

        if (this.connectionSettings != null){
            mainRoot.put(FIELD_SETTINGS, this.connectionSettings.toJSON(null));
        }

        final StringBuilder sb = new StringBuilder(this.endpointInfo.getConnectionString());
        if (mainRoot.length() == 0){
            return sb.toString();
        }

        sb.append("/?");

        final List<ConfigEntry> configEntries = serializeObject(mainRoot, null, null);
        boolean first = true;
        for(ConfigEntry ce : configEntries){
            if (!first){
                sb.append("&");
            }

            try {
                sb.append(URLEncoder.encode(ce.getCompleteKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(ce.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("To URL transformation exception", e);
            }

            first = false;
        }

        return sb.toString();
    }

    /**
     * Returns root JSON objects with serialized settings.
     * Contains main settings and elements.
     *
     * @return root JSON object
     */
    public JSONObject getJsonRoot() {
        return new JSONObject(jsonRoot);
    }

    /**
     * Returns sub JSON object from the root JSON configuration object
     * by the key. Elements are serialized using addElement(elem, key).
     * This call is for reversal process - deserialization.
     *
     * @param field key to extract. If contains dot, considered as path (package like).
     * @return
     */
    public JSONObject getElement(String field){
        final String[] path = field.split("\\.");
        final String last = path[path.length-1];
        final JSONObject parent = getParentNode(jsonRoot, path, false);
        return parent != null && parent.has(last) ? parent.getJSONObject(last) : null;
    }

    /**
     * Deserializes flat URL configuration hierarchy to the JSON object.
     *
     * @param queryString URL query string
     * @return root JSON object
     * @throws UnsupportedEncodingException
     */
    public static JSONObject deserializeData(String queryString) throws UnsupportedEncodingException {
        if (queryString.startsWith("?")){
            queryString = queryString.substring(1);
        }

        final String[] parts = queryString.split("&");
        final JSONObject root = new JSONObject();

        final Map<String, String> options = new HashMap<String, String>();
        final Map<String, List<String>> optionsList = new HashMap<String, List<String>>();

        // Pre-parse
        for(String part : parts){
            final String[] hdrAndVal = part.split("=", 2);
            final String hdr = URLDecoder.decode(hdrAndVal[0], "UTF-8");
            final String val = URLDecoder.decode(hdrAndVal[1], "UTF-8");

            if (hdr.endsWith("[]")){
                final String newHdr = hdr.replaceAll("\\[\\]$", "");
                List<String> lst = optionsList.get(newHdr);
                if (lst == null){
                    lst = new LinkedList<String>();
                }

                lst.add(val);
                optionsList.put(newHdr, lst);

            } else {
                options.put(hdr, val);
            }
        }

        for (Map.Entry<String, String> eset : options.entrySet()) {
            final String hdr = eset.getKey();
            final String val = eset.getValue();
            final String[] path = hdr.split("\\.");
            final JSONObject parent = getParentNode(root, path);
            parent.put(path[path.length-1], val);
        }

        for (Map.Entry<String, List<String>> eset : optionsList.entrySet()) {
            final String hdr = eset.getKey();
            final List<String> lst = eset.getValue();
            final String[] path = hdr.split("\\.");
            final JSONObject parent = getParentNode(root, path);
            final StringBuilder sb = new StringBuilder().append("[");
            boolean first = true;
            for(String str : lst){
                if (!first){
                    sb.append(",");
                }

                sb.append(str);
                first = false;
            }

            parent.put(path[path.length-1], new JSONArray(sb.toString()));
        }

        return root;
    }

    /**
     * Returns corresponding JSON object when traversed from root using path of the keys.
     * If object does not exists, it is created. Similar to mkdir -p path
     *
     * @param root JSON object to start traversal.
     * @param path path down the hierarchy from root
     * @return JSONObject
     */
    public static JSONObject getParentNode(JSONObject root, String[] path){
        return getParentNode(root, path, true);
    }

    /**
     * Returns corresponding JSON object when traversed from root using path of the keys.
     * If object does not exists, it is created. Similar to mkdir -p path
     *
     * @param root JSON object to start traversal.
     * @param path path down the hierarchy from root
     * @param createIfDoesNotExist if true, behaves like mkdir -p, otherwise returns null.
     * @return JSONObject
     */
    public static JSONObject getParentNode(JSONObject root, String[] path, boolean createIfDoesNotExist){
        if (path.length == 1){
            return root;
        }

        JSONObject current = root;
        for(int i = 0, ln = path.length - 1; i < ln; ++i){
            if (current.has(path[i])){
                current = current.getJSONObject(path[i]);
            } else if (createIfDoesNotExist) {
                current.put(path[i], new JSONObject());
                current = current.getJSONObject(path[i]);
            } else {
                return null;
            }
        }

        return current;
    }

    public static String nodeMakePath(String key, String parent){
        if (key == null && parent == null){
            return null;
        } else if (key != null && parent != null){
            return parent + "." + key;
        } else if (key != null){
            return key;
        } else {
            return parent;
        }
    }

    /**
     * Recursive serialization routine. Flattens JSONObject hierarchy to the collection of ConfigEntry.
     * It is then used to construct URL configuration.
     *
     * @param obj object to process
     * @param key key for the object
     * @param parent parent path, dot separated (as packages)
     * @return list of config entries generated by processing the object recursively.
     */
    public static List<ConfigEntry> serializeObject(Object obj, String key, String parent){
        if (obj == null){
            return Collections.emptyList();
        }

        List<ConfigEntry> config = new LinkedList<ConfigEntry>();
        if (obj instanceof JSONObject){
            final JSONObject jObj = (JSONObject) obj;
            final String[] names = JSONObject.getNames(jObj);

            if (names != null && names.length != 0) {
                for (String ckey : names) {
                    final Object o = jObj.get(ckey);

                    config.addAll(serializeObject(o, ckey, nodeMakePath(key, parent)));
                }
            }

        } else if (obj instanceof JSONArray) {
            final JSONArray ar = (JSONArray) obj;
            final List<String> lst = new LinkedList<String>();

            for(int i = 0, ln = ar.length(); i < ln; ++i){
                final Object o = ar.get(i);
                // JSONObject && JSONArray are not allowed. Sorry.
                if (o instanceof JSONObject || o instanceof JSONArray){
                    lst.add(o.toString());
                } else {
                    for (ConfigEntry ce : serializeObject(o, key, parent)){
                        lst.add(ce.getValue());
                    }
                }
            }

            for(String str : lst) {
                config.add(new ConfigEntry(key + "[]", str, parent));
            }

        } else if (obj instanceof Collection) {
            final Collection c = (Collection) obj;
            final List<String> lst = new LinkedList<String>();

            for (Object o : c) {
                // JSONObject && JSONArray are not allowed. Sorry.
                if (o instanceof JSONObject || o instanceof JSONArray){
                    lst.add(o.toString());
                } else {
                    for (ConfigEntry ce : serializeObject(o, key, parent)){
                        lst.add(ce.getValue());
                    }
                }
            }

            for(String str : lst) {
                config.add(new ConfigEntry(key + "[]", str, parent));
            }

        } else if (obj instanceof String){
            config.add(new ConfigEntry(key, (String) obj, parent));

        } else if (obj instanceof Number){
            config.add(new ConfigEntry(key, obj.toString(), parent));

        } else if (obj instanceof Boolean){
            config.add(new ConfigEntry(key, obj.toString(), parent));

        } else {
            config.add(new ConfigEntry(key, obj.toString(), parent));

        }

        return config;
    }

    // Setters

    protected void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    protected void setEndpointInfo(EBEndpointInfo endpointInfo) {
        this.endpointInfo = endpointInfo;
    }

    protected void setConnectionSettings(EBConnectionSettings connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    /**
     * Stores config entry after conversion from JSON object.
     */
    public static class ConfigEntry {
        protected String key;
        protected String value;
        protected String parent;

        public ConfigEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public ConfigEntry(String key, String value, String parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        public String getCompleteKey(){
            return parent == null ? key : parent + "." + key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }
    }
}
