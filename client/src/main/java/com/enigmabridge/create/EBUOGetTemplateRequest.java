package com.enigmabridge.create;

import com.enigmabridge.UserObjectType;
import com.enigmabridge.comm.EBRawRequest;

/**
 * GetTemplateUO request.
 * Created by dusanklinec on 28.06.16.
 */
public class EBUOGetTemplateRequest extends EBRawRequest {
    protected int format = 1;        //<integer, starting with 1>,
    protected int protocol = 1;      //<integer, starting with 1>,
    protected long type = UserObjectType.getValue(
            UserObjectType.TYPE_PLAINAES,
            Constants.GENKEY_LEGACY_ENROLL_RANDOM,
            Constants.GENKEY_CLIENT); //<32bit integer>,

    protected String environment = Constants.ENV_DEV; // shows whether the UO should be for production (live), test (pre-production testing), or dev (development)
    protected String maxtps = Constants.MAXTPS_UNLIMITED; // maximum guaranteed TPS
    protected String core = Constants.CORE_EMPTY; // how many cards have UO loaded permanently
    protected String persistence = Constants.PERSISTENCE_1MIN; // once loaded onto card, how long will the UO stay there without use (this excludes the "core")
    protected String priority = Constants.PRIORITY_DEFAULT; // this defines a) priority when the server capacity is fully utilised and it also defines how quickly new copies of UO are installed (pre-empting icreasing demand)
    protected String separation = Constants.SEPARATION_TIME; // "complete" = only one UO can be loaded on a smartcard at one one time
    protected String bcr = Constants.YES;      // "yes" will ensure the UO is replicated to provide high availability for any possible service disruption
    protected String unlimited = Constants.YES;
    protected String clientiv = Constants.YES; //  if "yes", we expect the data starts with an IV to initialize decryption of data - this is for communication security
    protected String clientdiv = Constants.NO; // if "yes", we expect the data starting with a diversification 16B for communication keys
    protected String resource = Constants.RESOURCE_GLOBAL;
    protected int credit = 256; // <1-32767>, a limit a seed card can provide to the EB service
    protected int generationCommKey = Constants.GENKEY_CLIENT;
    protected int generationBillingKey = Constants.GENKEY_LEGACY_ENROLL_RANDOM;
    protected int generationAppKey = Constants.GENKEY_LEGACY_ENROLL_RANDOM;

    public int getFormat() {
        return format;
    }

    public EBUOGetTemplateRequest setFormat(int format) {
        this.format = format;
        return this;
    }

    public int getProtocol() {
        return protocol;
    }

    public EBUOGetTemplateRequest setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    public long getType() {
        return type;
    }

    public EBUOGetTemplateRequest setType(long type) {
        setType(UserObjectType.valueOf(type));
        return this;
    }

    public EBUOGetTemplateRequest setType(UserObjectType type) {
        this.type = type.getValue();
        generationAppKey = type.getAppKeyGenerationType();
        generationCommKey = type.getComKeyGenerationType();
        return this;
    }

    public EBUOGetTemplateRequest setTypeFunction(int typeFunction) {
        UserObjectType newType = new UserObjectType.Builder()
                .setUoType(this.getType())
                .setUoTypeFunction(typeFunction)
                .build();

        this.type = newType.getValue();
        generationAppKey = newType.getAppKeyGenerationType();
        generationCommKey = newType.getComKeyGenerationType();
        return this;
    }

    public String getEnvironment() {
        return environment;
    }

    public EBUOGetTemplateRequest setEnvironment(String environment) {
        this.environment = environment;
        return this;
    }

    public String getMaxtps() {
        return maxtps;
    }

    public EBUOGetTemplateRequest setMaxtps(String maxtps) {
        this.maxtps = maxtps;
        return this;
    }

    public String getCore() {
        return core;
    }

    public EBUOGetTemplateRequest setCore(String core) {
        this.core = core;
        return this;
    }

    public String getPersistence() {
        return persistence;
    }

    public EBUOGetTemplateRequest setPersistence(String persistence) {
        this.persistence = persistence;
        return this;
    }

    public String getPriority() {
        return priority;
    }

    public EBUOGetTemplateRequest setPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public String getSeparation() {
        return separation;
    }

    public EBUOGetTemplateRequest setSeparation(String separation) {
        this.separation = separation;
        return this;
    }

    public String getBcr() {
        return bcr;
    }

    public EBUOGetTemplateRequest setBcr(String bcr) {
        this.bcr = bcr;
        return this;
    }

    public String getUnlimited() {
        return unlimited;
    }

    public EBUOGetTemplateRequest setUnlimited(String unlimited) {
        this.unlimited = unlimited;
        return this;
    }

    public String getClientiv() {
        return clientiv;
    }

    public EBUOGetTemplateRequest setClientiv(String clientiv) {
        this.clientiv = clientiv;
        return this;
    }

    public String getClientdiv() {
        return clientdiv;
    }

    public EBUOGetTemplateRequest setClientdiv(String clientdiv) {
        this.clientdiv = clientdiv;
        return this;
    }

    public String getResource() {
        return resource;
    }

    public EBUOGetTemplateRequest setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public int getCredit() {
        return credit;
    }

    public EBUOGetTemplateRequest setCredit(int credit) {
        this.credit = credit;
        return this;
    }

    public int getGenerationCommKey() {
        return generationCommKey;
    }

    public EBUOGetTemplateRequest setGenerationCommKey(int generationCommKey) {
        this.generationCommKey = generationCommKey;
        this.type = UserObjectType.getValue((int)type, generationAppKey, generationCommKey);

        return this;
    }

    public int getGenerationBillingKey() {
        return generationBillingKey;
    }

    public EBUOGetTemplateRequest setGenerationBillingKey(int generationBillingKey) {
        this.generationBillingKey = generationBillingKey;
        return this;
    }

    public int getGenerationAppKey() {
        return generationAppKey;
    }

    public EBUOGetTemplateRequest setGenerationAppKey(int generationAppKey) {
        this.generationAppKey = generationAppKey;
        this.type = UserObjectType.getValue((int)type, generationAppKey, generationCommKey);

        return this;
    }
}
