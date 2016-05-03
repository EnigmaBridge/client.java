package com.enigmabridge.comm;

/**
 * Raw request to EB.
 * Created by dusanklinec on 28.04.16.
 */
public class EBRawRequest {
    protected String method = EBCommUtils.METHOD_DEFAULT;
    protected String query;
    protected String body;

    @Override
    public String toString() {
        return "EBRawRequest{" +
                "method='" + method + '\'' +
                ", query='" + query + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
