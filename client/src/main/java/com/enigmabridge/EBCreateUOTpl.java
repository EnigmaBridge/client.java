package com.enigmabridge;

import com.enigmabridge.create.Constants;
import com.enigmabridge.utils.FieldWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import static com.enigmabridge.EBUtils.*;

/**
 * Create UO default template.
 *
 * Created by dusanklinec on 07.11.16.
 */
public class EBCreateUOTpl extends JSONObject implements EBJSONSerializable{
    // Public JSON constants
    public static final String FIELD_FORMAT = "format";
    public static final String FIELD_PROTOCOL = "protocol";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_ENVIRONMENT = "environment";
    public static final String FIELD_MAXTPS = "maxtps";
    public static final String FIELD_CORE = "core";
    public static final String FIELD_PERSISTENCE = "persistence";
    public static final String FIELD_PRIORITY = "priority";
    public static final String FIELD_SEPARATION = "separation";
    public static final String FIELD_BCR = "bcr";
    public static final String FIELD_UNLIMITED = "unlimited";
    public static final String FIELD_CLIENTIV = "clientiv";
    public static final String FIELD_CLIENTDIV = "clientdiv";
    public static final String FIELD_RESOURCE = "resource";
    public static final String FIELD_CREDIT = "credit";
    public static final String FIELD_GENERATION = "generation";
    public static final String FIELD_GENERATION_COMMKEY = "commkey";
    public static final String FIELD_GENERATION_BILLINGKEY = "billingkey";
    public static final String FIELD_GENERATION_APPKEY = "appkey";

    // Attributes
    //<integer, starting with 1>,
    protected final FieldWrapper<Integer> format =
            new FieldWrapper<Integer>(1, true);

    //<integer, starting with 1>,
    protected final FieldWrapper<Integer> protocol =
            new FieldWrapper<Integer>(1, true);

    //<32bit integer>,
    protected final FieldWrapper<Long> type = new FieldWrapper<Long>(UserObjectType.getValue(
            UserObjectType.TYPE_PLAINAES,
            Constants.GENKEY_LEGACY_ENROLL_RANDOM,
            Constants.GENKEY_CLIENT), true);

    // shows whether the UO should be for production (live), test (pre-production testing), or dev (development)
    protected final FieldWrapper<String> environment =
            new FieldWrapper<String>(Constants.ENV_DEV, true);

    // maximum guaranteed TPS
    protected final FieldWrapper<String> maxtps =
            new FieldWrapper<String>(Constants.MAXTPS_1, true);

    // how many cards have UO loaded permanently
    protected final FieldWrapper<String> core =
            new FieldWrapper<String>(Constants.CORE_EMPTY, true);

    // once loaded onto card, how long will the UO stay there without use (this excludes the "core")
    protected final FieldWrapper<String> persistence =
            new FieldWrapper<String>(Constants.PERSISTENCE_1MIN, true);

    // this defines a) priority when the server capacity is fully utilised and it also defines how quickly new copies of UO are installed (pre-empting icreasing demand)
    protected final FieldWrapper<String> priority =
            new FieldWrapper<String>(Constants.PRIORITY_DEFAULT, true);

    // "complete" = only one UO can be loaded on a smartcard at one one time
    protected final FieldWrapper<String> separation =
            new FieldWrapper<String>(Constants.SEPARATION_TIME, true);

    // "yes" will ensure the UO is replicated to provide high availability for any possible service disruption
    protected final FieldWrapper<String> bcr =
            new FieldWrapper<String>(Constants.YES, true);

    protected final FieldWrapper<String> unlimited =
            new FieldWrapper<String>(Constants.YES, true);

    //  if "yes", we expect the data starts with an IV to initialize decryption of data - this is for communication security
    protected final FieldWrapper<String> clientiv =
            new FieldWrapper<String>(Constants.YES, true);

    // if "yes", we expect the data starting with a diversification 16B for communication keys
    protected final FieldWrapper<String> clientdiv =
            new FieldWrapper<String>(Constants.NO, true);

    protected final FieldWrapper<String> resource =
            new FieldWrapper<String>(Constants.RESOURCE_GLOBAL, true);

    // <1-32767>, a limit a seed card can provide to the EB service
    protected final FieldWrapper<Integer> credit =
            new FieldWrapper<Integer>(256, true);

    protected final FieldWrapper<Integer> generationCommKey =
            new FieldWrapper<Integer>(Constants.GENKEY_CLIENT, true);

    protected final FieldWrapper<Integer> generationBillingKey =
            new FieldWrapper<Integer>(Constants.GENKEY_LEGACY_ENROLL_RANDOM, true);

    protected final FieldWrapper<Integer> generationAppKey =
            new FieldWrapper<Integer>(Constants.GENKEY_LEGACY_ENROLL_RANDOM, true);

    public EBCreateUOTpl() {
    }

    public EBCreateUOTpl(String source) throws JSONException {
        super(source);
    }

    public EBCreateUOTpl(JSONObject tplJson) {
        fromJSON(tplJson);
    }

    public void fromJSON(JSONObject obj){
        if (obj == null){
            throw new NullPointerException("Cannot load from null JSONObject");
        }

        // Copy JSON settings.
        EBUtils.mergeInto(obj, this);

        // Absorb existing fields to attributes
        absorbIntFieldValue(obj, FIELD_FORMAT, format);
        absorbIntFieldValue(obj, FIELD_PROTOCOL, protocol);
        absorbLongFieldValue(obj, FIELD_TYPE, type);

        absorbStringFieldValue(obj, FIELD_ENVIRONMENT, environment);
        absorbStringFieldValue(obj, FIELD_MAXTPS, maxtps);
        absorbStringFieldValue(obj, FIELD_CORE, core);
        absorbStringFieldValue(obj, FIELD_PERSISTENCE, persistence);
        absorbStringFieldValue(obj, FIELD_PRIORITY, priority);
        absorbStringFieldValue(obj, FIELD_SEPARATION, separation);
        absorbStringFieldValue(obj, FIELD_BCR, bcr);
        absorbStringFieldValue(obj, FIELD_UNLIMITED, unlimited);
        absorbStringFieldValue(obj, FIELD_CLIENTIV, clientiv);
        absorbStringFieldValue(obj, FIELD_CLIENTDIV, clientdiv);
        absorbStringFieldValue(obj, FIELD_RESOURCE, resource);
        absorbIntFieldValue(obj, FIELD_CREDIT, credit);

        if (obj.has(FIELD_GENERATION)){
            final JSONObject gen = obj.getJSONObject(FIELD_GENERATION);
            absorbIntFieldValue(gen, FIELD_GENERATION_COMMKEY, generationCommKey);
            absorbIntFieldValue(gen, FIELD_GENERATION_BILLINGKEY, generationBillingKey);
            absorbIntFieldValue(gen, FIELD_GENERATION_APPKEY, generationAppKey);
        }
    }

    @Override
    public JSONObject toJSON(JSONObject json) {
        return toJSON(json, false);
    }

    public JSONObject toJSON(JSONObject obj, boolean addDefaults){
        if (obj == null){
            obj = new JSONObject();
        }

        // Absorb existing fields to attributes
        addToJSON(obj, FIELD_FORMAT, format, addDefaults);
        addToJSON(obj, FIELD_PROTOCOL, protocol, addDefaults);
        addToJSON(obj, FIELD_TYPE, type, addDefaults);

        addToJSON(obj, FIELD_ENVIRONMENT, environment, addDefaults);
        addToJSON(obj, FIELD_MAXTPS, maxtps, addDefaults);
        addToJSON(obj, FIELD_CORE, core, addDefaults);
        addToJSON(obj, FIELD_PERSISTENCE, persistence, addDefaults);
        addToJSON(obj, FIELD_PRIORITY, priority, addDefaults);
        addToJSON(obj, FIELD_SEPARATION, separation, addDefaults);
        addToJSON(obj, FIELD_BCR, bcr, addDefaults);
        addToJSON(obj, FIELD_UNLIMITED, unlimited, addDefaults);
        addToJSON(obj, FIELD_CLIENTIV, clientiv, addDefaults);
        addToJSON(obj, FIELD_CLIENTDIV, clientdiv, addDefaults);
        addToJSON(obj, FIELD_RESOURCE, resource, addDefaults);
        addToJSON(obj, FIELD_CREDIT, credit, addDefaults);

        if (addDefaults || (
                !generationCommKey.isDefault() ||
                        !generationBillingKey.isDefault() ||
                        !generationAppKey.isDefault() ))
        {
            JSONObject generation = new JSONObject();
            if (obj.has(FIELD_GENERATION)){
                generation = obj.getJSONObject(FIELD_GENERATION);
            }

            addToJSON(generation, FIELD_GENERATION_COMMKEY, generationCommKey, addDefaults);
            addToJSON(generation, FIELD_GENERATION_BILLINGKEY, generationBillingKey, addDefaults);
            addToJSON(generation, FIELD_GENERATION_APPKEY, generationAppKey, addDefaults);

            obj.put(FIELD_GENERATION, generation);
        }

        return obj;
    }

    protected void addToJSON(JSONObject obj, String key, FieldWrapper field, boolean addDefault){
        if (!addDefault && field.isDefault()){
            return;
        }

        obj.put(key, field.getValue());
    }

    /**
     * Returns true if all properties are in the default value
     * @return true if all fields are in defaults.
     */
    public boolean isAllDefault(){
        return
                format.isDefault()
                && protocol.isDefault()
                && type.isDefault()
                && environment.isDefault()
                && maxtps.isDefault()
                && core.isDefault()
                && persistence.isDefault()
                && priority.isDefault()
                && separation.isDefault()
                && bcr.isDefault()
                && unlimited.isDefault()
                && clientiv.isDefault()
                && clientdiv.isDefault()
                && resource.isDefault()
                && credit.isDefault()
                && generationCommKey.isDefault()
                && generationBillingKey.isDefault()
                && generationAppKey.isDefault();
    }

    // Raw access getters for isDefault

    public FieldWrapper<Integer> getFormatRaw() {
        return format;
    }

    public FieldWrapper<Integer> getProtocolRaw() {
        return protocol;
    }

    public FieldWrapper<Long> getTypeRaw() {
        return type;
    }

    public FieldWrapper<String> getEnvironmentRaw() {
        return environment;
    }

    public FieldWrapper<String> getMaxtpsRaw() {
        return maxtps;
    }

    public FieldWrapper<String> getCoreRaw() {
        return core;
    }

    public FieldWrapper<String> getPersistenceRaw() {
        return persistence;
    }

    public FieldWrapper<String> getPriorityRaw() {
        return priority;
    }

    public FieldWrapper<String> getSeparationRaw() {
        return separation;
    }

    public FieldWrapper<String> getBcrRaw() {
        return bcr;
    }

    public FieldWrapper<String> getUnlimitedRaw() {
        return unlimited;
    }

    public FieldWrapper<String> getClientivRaw() {
        return clientiv;
    }

    public FieldWrapper<String> getClientdivRaw() {
        return clientdiv;
    }

    public FieldWrapper<String> getResourceRaw() {
        return resource;
    }

    public FieldWrapper<Integer> getCreditRaw() {
        return credit;
    }

    public FieldWrapper<Integer> getGenerationCommKeyRaw() {
        return generationCommKey;
    }

    public FieldWrapper<Integer> getGenerationBillingKeyRaw() {
        return generationBillingKey;
    }

    public FieldWrapper<Integer> getGenerationAppKeyRaw() {
        return generationAppKey;
    }

    // Getters on wrapper

    public int getFormat() {
        return format.getValue();
    }

    public int getProtocol() {
        return protocol.getValue();
    }

    public long getType() {
        return type.getValue();
    }

    public String getEnvironment() {
        return environment.getValue();
    }

    public String getMaxtps() {
        return maxtps.getValue();
    }

    public String getCore() {
        return core.getValue();
    }

    public String getPersistence() {
        return persistence.getValue();
    }

    public String getPriority() {
        return priority.getValue();
    }

    public String getSeparation() {
        return separation.getValue();
    }

    public String getBcr() {
        return bcr.getValue();
    }

    public String getUnlimited() {
        return unlimited.getValue();
    }

    public String getClientiv() {
        return clientiv.getValue();
    }

    public String getClientdiv() {
        return clientdiv.getValue();
    }

    public String getResource() {
        return resource.getValue();
    }

    public int getCredit() {
        return credit.getValue();
    }

    public int getGenerationCommKey() {
        return generationCommKey.getValue();
    }

    public int getGenerationBillingKey() {
        return generationBillingKey.getValue();
    }

    public int getGenerationAppKey() {
        return generationAppKey.getValue();
    }
}
