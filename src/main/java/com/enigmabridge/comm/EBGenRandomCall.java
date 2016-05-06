package com.enigmabridge.comm;

import com.enigmabridge.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Call for generating random numbers in SE.
 * Created by dusanklinec on 06.05.16.
 */
public class EBGenRandomCall {
    private static final Logger LOG = LoggerFactory.getLogger(EBGenRandomCall.class);

    /**
     * ProcessData builder, for builder approach.
     */
    private final EBProcessDataCall.Builder processDataBuilder = new EBProcessDataCall.Builder();
    private EBProcessDataCall processDataCaller;

    /**
     * Last response from the server.
     */
    private EBGenRandomResponse lastResponse;

    /**
     * Separate abstract builder.
     * @param <T>
     * @param <B>
     */
    public static abstract class AbstractBuilder<T extends EBGenRandomCall, B extends AbstractBuilder> {
        public B setSettings(EBSettings settings) {
            getObj().getProcessDataBuilder().setSettings(settings);
            return getThisBuilder();
        }

        public B setUo(UserObjectInfo uo){
            getObj().getProcessDataBuilder().setUo(uo);
            return getThisBuilder();
        }

        public B setEngine(EBEngine engine){
            getObj().getProcessDataBuilder().setEngine(engine);
            return getThisBuilder();
        }

        public abstract T build();
        public abstract B getThisBuilder();
        public abstract T getObj();
    }

    public static class Builder extends AbstractBuilder<EBGenRandomCall, Builder> {
        private final EBGenRandomCall child = new EBGenRandomCall();

        @Override
        public EBGenRandomCall getObj() {
            return child;
        }

        @Override
        public EBGenRandomCall build() {
            // Check if UO is set
            getObj().getProcessDataBuilder().setProcessFunction(EBRequestType.RANDOMDATA);
            return child;
        }

        @Override
        public Builder getThisBuilder() {
            return this;
        }
    }

    EBGenRandomCall() {
    }

    public EBGenRandomCall(EBEngine engine, UserObjectInfo uo) {
        processDataBuilder.setUo(uo).setEngine(engine);
    }

    /**
     * Main method for generating randomness.
     * @param length
     * @return
     */
    public EBGenRandomResponse genRandom(int length) throws IOException, EBCorruptedException {
        EBGenRandomResponse resp = new EBGenRandomResponse();
        if (processDataCaller == null){
            processDataCaller = getProcessDataBuilder().build();
        }

        byte[] reqData = new byte[2];
        EBCommUtils.setShort(reqData, 0, (short)length);

        final EBProcessDataResponse pdResp = processDataCaller.doRequest(reqData);

        resp.setRawResponse(pdResp.getRawResponse());
        resp.setStatusCode(pdResp.getStatusCode());
        resp.setStatusDetail(pdResp.getStatusDetail());
        if (pdResp.isCodeOk()){
            resp.setData(pdResp.getProtectedData());
        }

        return resp;
    }

    /**
     * Generates random data remotely.
     * @param length
     * @return
     * @throws IOException
     * @throws EBException
     */
    public byte[] genRandomData(int length) throws IOException, EBException {
        lastResponse = genRandom(length);
        if (!lastResponse.isCodeOk()){
            throw new EBException("Could not generate random data");
        }

        return lastResponse.getData();
    }

    public EBGenRandomResponse getLastResponse() {
        return lastResponse;
    }

    EBProcessDataCall.Builder getProcessDataBuilder() {
        return processDataBuilder;
    }
}
