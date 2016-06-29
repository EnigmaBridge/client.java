package com.enigmabridge.create;

/**
 *
 * {"type": "commk",  offset: 180, length: 20,  "tlvtype":1},
 * {"type": "comenc",  offset: 200, length: 10,  "tlvtype":1},
 * {"type": "commac",  offset: 210, length: 10,  "tlvtype":2},
 * {"type": "billing", offset: 220, length: 10,  "tlvtype":3},
 * {"type": "comnextenc", offset: 230, length: 10,  "tlvtype":4},
 * {"type": "conextmac", offset: 240, length: 10,  "tlvtype":5},
 * {"type": "app",    offset: 250, length: 200, "tlvtype":6}
 *
 * Created by dusanklinec on 28.06.16.
 */
public class EBUOTemplateKeyOffset {
    private String type;
    private long offset;
    private long length;
    private int tlvtype;

    public static abstract class AbstractBuilder<T extends EBUOTemplateKeyOffset, B extends EBUOTemplateKeyOffset.AbstractBuilder> {
        public B setType(String type) {
            getObj().setType(type);
            return getThisBuilder();
        }

        public B setOffset(long offset) {
            getObj().setOffset(offset);
            return getThisBuilder();
        }

        public B setLength(long length) {
            getObj().setLength(length);
            return getThisBuilder();
        }

        public B setTlvtype(int tlvtype) {
            getObj().setTlvtype(tlvtype);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends EBUOTemplateKeyOffset.AbstractBuilder<EBUOTemplateKeyOffset, EBUOTemplateKeyOffset.Builder> {
        private final EBUOTemplateKeyOffset parent = new EBUOTemplateKeyOffset();

        @Override
        public EBUOTemplateKeyOffset.Builder getThisBuilder() {
            return this;
        }

        @Override
        public EBUOTemplateKeyOffset getObj() {
            return parent;
        }

        @Override
        public EBUOTemplateKeyOffset build() {
            return parent;
        }
    }

    // Setters

    protected void setType(String type) {
        this.type = type;
    }

    protected void setOffset(long offset) {
        this.offset = offset;
    }

    protected void setLength(long length) {
        this.length = length;
    }

    protected void setTlvtype(int tlvtype) {
        this.tlvtype = tlvtype;
    }

    // Getters

    public String getType() {
        return type;
    }

    public long getOffset() {
        return offset;
    }

    public long getLength() {
        return length;
    }

    public int getTlvtype() {
        return tlvtype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EBUOTemplateKeyOffset that = (EBUOTemplateKeyOffset) o;

        if (offset != that.offset) return false;
        if (length != that.length) return false;
        if (tlvtype != that.tlvtype) return false;
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (int) (offset ^ (offset >>> 32));
        result = 31 * result + (int) (length ^ (length >>> 32));
        result = 31 * result + tlvtype;
        return result;
    }

    @Override
    public String toString() {
        return "EBUOTemplateKeyOffset{" +
                "type='" + type + '\'' +
                ", offset=" + offset +
                ", length=" + length +
                ", tlvtype=" + tlvtype +
                '}';
    }
}
