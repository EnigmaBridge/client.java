package com.enigmabridge;

import java.io.*;
import java.util.Arrays;

/**
 * Holder represents user object and all required parameters for using it.
 *
 * Created by dusanklinec on 21.04.16.
 */
public class UserObjectInfo implements Serializable {
    public static final long serialVersionUID = 1L;

    /**
     * User object handle.
     */
    protected long uoid;

    /**
     * AES-256-CBC end-to-end encryption key.
     */
    protected byte[] encKey;

    /**
     * HMAC-AES-256-CBC key for end-to-end HMAC.
     */
    protected byte[] macKey;

    /**
     * API key for using EB service.
     */
    protected String apiKey;

    /**
     * Connection string to the EB endpoint
     * https://site1.enigmabridge.com:11180
     */
    protected EBEndpointInfo endpointInfo;

    public UserObjectInfo() {
    }

    public UserObjectInfo(long uoid) {
        this.uoid = uoid;
    }

    public UserObjectInfo(long uoid, byte[] encKey, byte[] macKey) {
        this.uoid = uoid;
        this.encKey = encKey;
        this.macKey = macKey;
    }

    public UserObjectInfo(long uoid, byte[] encKey, byte[] macKey, String apiKey) {
        this.uoid = uoid;
        this.encKey = encKey;
        this.macKey = macKey;
        this.apiKey = apiKey;
    }

    public UserObjectInfo(long uoid, byte[] encKey, byte[] macKey, String apiKey, String endpointInfo) {
        this.uoid = uoid;
        this.encKey = encKey;
        this.macKey = macKey;
        this.apiKey = apiKey;
        this.endpointInfo = endpointInfo;
    }

    /**
     * Builds UserObjectInfo from serialized form.
     * @param encoded byte representation of the object
     * @return new object loaded from byte representation
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static UserObjectInfo build(byte[] encoded) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = null;
        ObjectInput in = null;
        try {
            bis = new ByteArrayInputStream(encoded);
            in = new ObjectInputStream(bis);
            return (UserObjectInfo) in.readObject();

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * Serializes object to the byte array
     * @return encoded object representation.
     * @throws IOException
     */
    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            return bos.toByteArray();

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    @Override
    public String toString() {
        return "UserObjectInfo{" +
                "uoid=" + uoid +
                ", encKey=" + Arrays.toString(encKey) +
                ", macKey=" + Arrays.toString(macKey) +
                ", apiKey='" + apiKey + '\'' +
                ", endpointInfo='" + endpointInfo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserObjectInfo that = (UserObjectInfo) o;

        if (uoid != that.uoid) return false;
        if (!Arrays.equals(encKey, that.encKey)) return false;
        if (!Arrays.equals(macKey, that.macKey)) return false;
        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        return endpointInfo != null ? endpointInfo.equals(that.endpointInfo) : that.endpointInfo == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (uoid ^ (uoid >>> 32));
        result = 31 * result + Arrays.hashCode(encKey);
        result = 31 * result + Arrays.hashCode(macKey);
        result = 31 * result + (apiKey != null ? apiKey.hashCode() : 0);
        result = 31 * result + (endpointInfo != null ? endpointInfo.hashCode() : 0);
        return result;
    }

    public long getUoid() {
        return uoid;
    }

    public UserObjectInfo setUoid(long uoid) {
        this.uoid = uoid;
        return this;
    }

    public byte[] getEncKey() {
        return encKey;
    }

    public UserObjectInfo setEncKey(byte[] encKey) {
        this.encKey = encKey;
        return this;
    }

    public byte[] getMacKey() {
        return macKey;
    }

    public UserObjectInfo setMacKey(byte[] macKey) {
        this.macKey = macKey;
        return this;
    }

    public String getApiKey() {
        return apiKey;
    }

    public UserObjectInfo setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getEndpointInfo() {
        return endpointInfo;
    }

    public UserObjectInfo setEndpointInfo(String endpointInfo) {
        this.endpointInfo = endpointInfo;
        return this;
    }
}
