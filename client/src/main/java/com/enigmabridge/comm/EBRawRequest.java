package com.enigmabridge.comm;

import java.io.Serializable;

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
     */
    protected String path;

    /**
     * Request body
     */
    protected String body;

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
}
