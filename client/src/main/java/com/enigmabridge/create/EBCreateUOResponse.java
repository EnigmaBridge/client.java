package com.enigmabridge.create;

import com.enigmabridge.comm.EBResponse;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Create UO response from EB.
 *
 * Created by dusanklinec on 30.06.16.
 */
public class EBCreateUOResponse extends EBResponse {
    private EBUOHandle handle;
    private byte[] certificate;
    private byte[] publicKey;
    private byte[] signature;
    private List<byte[]> certificateChain;

    public static abstract class ABuilder<T extends EBCreateUOResponse, B extends EBCreateUOResponse.ABuilder>
            extends EBResponse.ABuilder<T,B>
    {
        public B setHandle(String handle) {
            getObj().setHandle(EBCreateUtils.getHandleObj(handle));
            return getThisBuilder();
        }

        public B setHandle(EBUOHandle handle) {
            getObj().setHandle(handle);
            return getThisBuilder();
        }

        public B setPublicKey(byte[] p) {
            getObj().setPublicKey(p);
            return getThisBuilder();
        }

        public B setSignature(byte[] s) {
            getObj().setSignature(s);
            return getThisBuilder();
        }

        public B setCertificate(byte[] certificate) {
            getObj().setCertificate(certificate);
            return getThisBuilder();
        }

        public B setCertificateChain(List<byte[]> certificateChain) {
            getObj().setCertificateChain(certificateChain);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends EBCreateUOResponse.ABuilder<EBCreateUOResponse, EBCreateUOResponse.Builder> {
        private final EBCreateUOResponse parent = new EBCreateUOResponse();

        @Override
        public EBCreateUOResponse.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBCreateUOResponse getObj() {
            return parent;
        }

        @Override
        public EBCreateUOResponse build() {
            return parent;
        }
    }

    // Setters

    public EBCreateUOResponse setHandle(EBUOHandle handle) {
        this.handle = handle;
        return this;
    }

    protected EBCreateUOResponse setCertificate(byte[] certificate) {
        this.certificate = certificate;
        return this;
    }

    protected EBCreateUOResponse setCertificateChain(List<byte[]> certificateChain) {
        this.certificateChain = certificateChain;
        return this;
    }

    protected EBCreateUOResponse setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    protected EBCreateUOResponse setSignature(byte[] signature) {
        this.signature = signature;
        return this;
    }

    // Getters

    public EBUOHandle getHandle() {
        return handle;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public List<byte[]> getCertificateChain() {
        if (certificateChain == null){
            certificateChain = new LinkedList<byte[]>();
        }
        return certificateChain;
    }

    // hashCode, equals, toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBCreateUOResponse that = (EBCreateUOResponse) o;

        if (handle != null ? !handle.equals(that.handle) : that.handle != null) return false;
        if (!Arrays.equals(certificate, that.certificate)) return false;
        if (!Arrays.equals(publicKey, that.publicKey)) return false;
        if (!Arrays.equals(signature, that.signature)) return false;
        return certificateChain != null ? certificateChain.equals(that.certificateChain) : that.certificateChain == null;

    }

    @Override
    public int hashCode() {
        int result = handle != null ? handle.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(certificate);
        result = 31 * result + Arrays.hashCode(publicKey);
        result = 31 * result + Arrays.hashCode(signature);
        result = 31 * result + (certificateChain != null ? certificateChain.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EBCreateUOResponse{" +
                "handle=" + handle +
                ", certificate=" + Arrays.toString(certificate) +
                ", publicKey=" + Arrays.toString(publicKey) +
                ", signature=" + Arrays.toString(signature) +
                ", certificateChain=" + certificateChain +
                "} " + super.toString();
    }
}
