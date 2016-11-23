package com.enigmabridge.comm;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Raw request to EB.
 * Created by dusanklinec on 28.04.16.
 */
public class EBRawRequest implements Serializable{
    public static final long serialVersionUID = 1L;

    /**
     * HTTP method to use for the request (POST/GET)
     */
    protected String method = EBCommUtils.METHOD_DEFAULT;

    /**
     * URL path. If POST is used, this is still added as the path segment.
     * By default scheme, host and port is taken from the endpoint defined in the connector - not here.
     */
    protected String path;

    /**
     * Request body
     */
    protected String body;

    /**
     * Header list, optional.
     */
    protected List<Header> headers;

    @Override
    public String toString() {
        return "EBRawRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Returns read-only header list.
     * @return read-only header list, non-null.
     */
    public List<Header> getHeaders() {
        if (headers == null){
            return Collections.unmodifiableList(Collections.<Header>emptyList());
        }

        return Collections.unmodifiableList(headers);
    }

    /**
     * Adds a new header to the request.
     *
     * @param name
     * @param value
     * @return request for fluent header adding.
     */
    public EBRawRequest addHeader(String name, String value){
        if (headers == null){
            headers = new LinkedList<Header>();
        }

        headers.add(new Header(name, value));
        return this;
    }

    /**
     * Header helper object
     */
    public static class Header {
        protected String name;
        protected String value;

        public Header() {
        }

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Header header = (Header) o;

            if (name != null ? !name.equals(header.name) : header.name != null) return false;
            return value != null ? value.equals(header.value) : header.value == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }
}
