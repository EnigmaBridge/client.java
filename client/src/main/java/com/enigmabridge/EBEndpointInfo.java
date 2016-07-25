package com.enigmabridge;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class represents EB endpoint info for queries.
 * E.g. https://site1.enigmabridge.com:11180
 *
 * Created by dusanklinec on 26.04.16.
 */
public class EBEndpointInfo implements Serializable, Cloneable {
    public static final long serialVersionUID = 1L;

    public static final int DEFAULT_PORT = 11180;
    public static final int DEFAULT_ENROLLMENT_PORT = 11182;
    public static final String DEFAULT_SCHEME = "https";
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile("^([a-zA-Z0-9])+://");

    private String scheme = DEFAULT_SCHEME;
    private String hostname = null;
    private int port = DEFAULT_PORT;

    public EBEndpointInfo() {
    }

    public EBEndpointInfo(String connectionString) throws MalformedURLException {
        setConnectionString(connectionString);
    }

    public EBEndpointInfo(String scheme, String hostname, int port) {
        this.scheme = scheme;
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Returns connection string.
     * E.g. https://site1.enigmabridge.com:11180
     * @return connection string
     */
    public String getConnectionString(){
        return getScheme() + "://" + getHostname() + ":" + getPort();
    }

    /**
     * Set a new connection string.
     * @param connectionString a new connection string to set
     * @return EndpointInfo (builder pattern)
     */
    public EBEndpointInfo setConnectionString(String connectionString) throws MalformedURLException {
        final Matcher m = PROTOCOL_PATTERN.matcher(connectionString);
        if (!m.find()){
            connectionString = DEFAULT_SCHEME + "://" + connectionString;
        }

        URL tmpUrl = new URL(connectionString);

        this.scheme = tmpUrl.getProtocol();
        this.port = tmpUrl.getPort() == -1 ? DEFAULT_PORT : tmpUrl.getPort();
        this.hostname = tmpUrl.getHost();
        return this;
    }

    public int getPort() {
        return port < 0 ? DEFAULT_PORT : port;
    }

    public EBEndpointInfo setPort(int port) {
        this.port = port;
        return this;
    }

    public String getScheme() {
        return scheme == null || scheme.isEmpty() ? DEFAULT_SCHEME : scheme;
    }

    public EBEndpointInfo setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    public EBEndpointInfo setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBEndpointInfo that = (EBEndpointInfo) o;

        if (port != that.port) return false;
        if (scheme != null ? !scheme.equals(that.scheme) : that.scheme != null) return false;
        return hostname != null ? hostname.equals(that.hostname) : that.hostname == null;

    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "EBEndpointInfo{" +
                "scheme='" + scheme + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                '}';
    }

    public EBEndpointInfo copy(){
        return new EBEndpointInfo(scheme, hostname, port);
    }
}
