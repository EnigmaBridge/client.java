package com.enigmabridge.comm;

/**
 * Communication statuses.
 * Created by dusanklinec on 27.04.16.
 */
public class EBCommStatus {
    /**
     * we use the highest 4 bits to signal one of our classes
     */
    public final static short ERROR_CLASS_CRITICAL      = (short) 0xf000;   // Serious general errors (e.g., general SE errors outside SHSM)
    public final static short ERROR_CLASS_WRONGDATA     = (short) 0x8000;   // Data with invalid structure are provided (lengths, formats, different packet...)
    public final static short ERROR_CLASS_SYNC          = (short) 0x4000;   // There is mismatch in believes (synchronization) between two entities. E.g., Controller thinks that given UO is on SE, but it is not
    public final static short ERROR_CLASS_SECURITY      = (short) 0x2000;   // Security related error, e.g., MAC is corrupted
    public final static short ERROR_CLASS_INFO          = (short) 0x1000;   // Error code with information only, can be ignored
    public final static short ERROR_CLASS_SECURITY_USER = (short) 0xa000;
    public final static short ERROR_CLASS_OTHER_SE      = (short) 0xb000;
    public final static short ERROR_CLASS_SECURE_CHANNEL= (short) 0xd000;


    //
    // Non-SHSM errors  (ISO7816...)
    //
    public final static short SW_STAT_OK =                  (short) 0x9000;


    //  There are errors caused by SE firmware - i.e., defined in ISO7816-x and other standards
    //  classes of these errors are:
    //  ISO7816 classes use highest byte (8 bits) to denote the class
    public final static short ERROR_CLASS_OK_GET_RESPONSE =         (short)0x6100; /** OK **/
    public final static short ERROR_CLASS_WRN_EEPROM_NOT_CHANGED =  (short)0x6200; /** Class used by Simona driver **/
    public final static short ERROR_CLASS_WRN_EEPROM_CHANGED =      (short)0x6300; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_EEPROM_NOT_CHANGED =  (short)0x6400; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_EEPROM_CHANGED =      (short)0x6500; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_CHECK_ERRORS_66 =     (short)0x6600; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_INVALID_LENGTH_67 =   (short)0x6700; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_CHECK_ERRORS_68 =     (short)0x6800; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_CHECK_ERRORS_69 =     (short)0x6900; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_NOT_FOUND_6a    =     (short)0x6a00; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_PARAM_ERROR_6b =      (short)0x6b00; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_CHECK_ERRORS_6c =     (short)0x6c00; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_UNKNOWN_COMMAND_6d =  (short)0x6d00; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_UNKNOWN_CLASS_6e =    (short)0x6e00; /** Error code defined by ISO7816 **/
    public final static short ERROR_CLASS_ERR_CHECK_ERRORS_6f =     (short)0x6f00; /** Usually a problem in crypto function **/


    //
    // SHSM errors
    //
    //////////////////////
    // ERROR_CLASS_CRITICAL
    public final static short SW_STAT_READER_ERROR =                (short)(ERROR_CLASS_CRITICAL | 0x002); /** never from SC **/
    public final static short SW_STAT_PROCESSING_ERROR =            (short)(ERROR_CLASS_CRITICAL | 0x003); /** never from SC **/
    public final static short SW_UNKNOWN_INTERNAL_STATE =           (short)(ERROR_CLASS_CRITICAL | 0x009); /** wrong state variable value - this should cause SE reset **/
    public final static short SW_EXECUTION_ERROR =                  (short)(ERROR_CLASS_CRITICAL | 0x011); /** error during SC command - reset worked(PS) **/
    public final static short SW_SE_FAILURE =                       (short)(ERROR_CLASS_CRITICAL | 0x012); /** SC already removed -  permanent failure of the SE - counter and delay is this repeats? **/
    public final static short SW_AUTHUSERCTX_WRAP_LEN_MISMATCH =    (short)(ERROR_CLASS_CRITICAL | 0x013); /** SC this should cause immediate reset -  mismatch of length of wrapped and newly wrapped blob **/
    public final static short SW_CRYPTO_ENGINE_NOT_ALLOCATED =      (short)(ERROR_CLASS_CRITICAL | 0x00e); /** crypto engine not allocated - reject process request **/
    public static final short SW_STAT_NO_PERMISSIONS =              (short)(ERROR_CLASS_CRITICAL | 0x015);
    public final static short SW_WRONG_INTERNAL_STATE =             (short)(ERROR_CLASS_CRITICAL | 0x018); /** from SC -&gt; reset **/
    public final static short SW_STAT_INVALID_USAGES_PROVIDED =     (short)(ERROR_CLASS_CRITICAL | 0x030); /** internal error of SC - unexpected negative number **/
    public final static short SW_STAT_NO_USER_OBJECT_VALUE =        (short)(ERROR_CLASS_CRITICAL | 0x035); /** set UO - no data - it must have passed crypto check **/
    public final static short SW_DATA_NOT_PROPERLY_ALLIGNED =       (short)(ERROR_CLASS_CRITICAL | 0x045); /** for derivation data for est. SC **/
    public final static short SW_EXTAPDU_IN_OUT_MISMATCH =          (short)(ERROR_CLASS_CRITICAL | 0x052); /** internal error - short buffer for long response **/
    public final static short SW_WRONG_DATA_LENGTH =                (short)(ERROR_CLASS_CRITICAL | 0x061); /** an error in parsing SE response - internal error? **/
    public final static short SW_STAT_NO_SUCH_USER_OBJECT =         (short)(ERROR_CLASS_CRITICAL | 0x076); /** non-existent UO requested for getUserObject, setUserObject **/


    // ERROR_CLASS_WRONGDATA
    public final static short SW_STAT_NO_SUCH_FUNCTION =            (short)(ERROR_CLASS_WRONGDATA | 0x001); /** from SC -&gt; reset **/
    public final static short SW_PROCESS_FUNCTION_NOT_IMPLEMENTED = (short)(ERROR_CLASS_WRONGDATA | 0x004); /** wrong UO - deserialize =&gt; reject UO **/
    public final static short SW_STAT_DATA_NOT_PROCESSED =          (short)(ERROR_CLASS_WRONGDATA | 0x007); /** not from SE - incorrect parsing of request in JSONRequest **/
    public final static short SW_BASESE_FUNCTION_NOT_ALLOWED =      (short)(ERROR_CLASS_WRONGDATA | 0x00c); /** command not implemented by this SC type **/
    public static final short SW_SC_WRONG_LENGTH =                  (short)(ERROR_CLASS_WRONGDATA | 0x016); /** wrong APDU length - terminal error **/
    public static final short SW_SC_WRONG_LENGTH_DATA =             (short)(ERROR_CLASS_WRONGDATA | 0x017);
    public final static short SW_STAT_INPUT_DATA_TOO_LONG =         (short)(ERROR_CLASS_WRONGDATA | 0x031); /** wrong format of input data - internal TLV **/
    public final static short SW_STAT_INPUT_DATA_WRONG_LENGTH =     (short)(ERROR_CLASS_WRONGDATA | 0x032); /** wrong format of input data - length of internal item **/
    public final static short SW_STAT_INVALID_KEY_TYPE =            (short)(ERROR_CLASS_WRONGDATA | 0x033); /** not from SE but it will be - a wrong template data provided **/
    public final static short SW_STAT_INVALID_USER_OBJECT_ID =      (short)(ERROR_CLASS_WRONGDATA | 0x034); /** processData with invalid UO **/
    public final static short SW_STAT_MISSING_OR_INVALID_ARGUMENT = (short)(ERROR_CLASS_WRONGDATA | 0x036); /** not from SE **/
    public final static short SW_STAT_INPUT_PARSE_FAIL =            (short)(ERROR_CLASS_WRONGDATA | 0x037); /** not from SE **/
    public final static short SW_STAT_INVALID_VERSION =             (short)(ERROR_CLASS_WRONGDATA | 0x038); /** not from SE **/
    public final static short SW_STAT_INVALID_PROCESS_DATA =        (short)(ERROR_CLASS_WRONGDATA | 0x039); /** not from SE **/
    public final static short SW_STAT_NO_NONCE =                    (short)(ERROR_CLASS_WRONGDATA | 0x03a); /** not from SE **/
    public final static short SW_STAT_INVALID_USER_OBJECT_VALUE =   (short)(ERROR_CLASS_WRONGDATA | 0x03b); /** not from SE **/
    public final static short SW_STAT_INVALID_PROCESS_DATA_ALG =    (short)(ERROR_CLASS_WRONGDATA | 0x03c); /** a wrong algorithm request **/
    public final static short SW_WRONG_PADDING =                    (short)(ERROR_CLASS_WRONGDATA | 0x03d); /** wrong padding values in request **/
    public final static short SW_WRONG_MAGIC_VALUE =                (short)(ERROR_CLASS_WRONGDATA | 0x03e); /** common sense format check for unwrapping secure channel state **/
    public final static short SW_STAT_INVALID_USEROBJECT_MAGIC =    (short)(ERROR_CLASS_WRONGDATA | 0x041); /** a control byte in a UO not correct **/
    public final static short SW_STAT_INVALID_UO_CREDIT_MAGIC =     (short)(ERROR_CLASS_WRONGDATA | 0x043); /** a control byte in a UO credit not correct **/
    public final static short SW_STAT_NOTEXPECTED_TARGETSETID =     (short)(ERROR_CLASS_WRONGDATA | 0x044); /** credits for a different SE ID **/
    public final static short SW_USER_OBJECT_WRONG_ENCRYPT_OFFSET = (short)(ERROR_CLASS_WRONGDATA | 0x047); /** UO format error - deserialize **/
    public final static short SW_USER_OBJECT_UNKNOWNRESOURCE =      (short)(ERROR_CLASS_WRONGDATA | 0x048); /** UO format error - item "source" **/
    public final static short SW_MAC_LENGTH_WRONG =                 (short)(ERROR_CLASS_WRONGDATA | 0x049); /** data for ProcessData **/
    public final static short SW_WRONG_SETAPDU_MAGIC_VALUE =        (short)(ERROR_CLASS_WRONGDATA | 0x04a); /** magic byte check failed **/
    public final static short SW_INVALID_TLV_FORMAT =               (short)(ERROR_CLASS_WRONGDATA | 0x04c); /** import of public/private key or TLV for authenticate **/
    public final static short SW_WRONG_PADDED_LENGTH =              (short)(ERROR_CLASS_WRONGDATA | 0x04e); /** data no padded - processdata **/
    public final static short SW_PADDING_VALUE_ERR =                (short)(ERROR_CLASS_WRONGDATA | 0x04f); /** incorrect format of padding - processdata **/
    public final static short SW_INSTALL_PARAMS_WRONG =             (short)(ERROR_CLASS_WRONGDATA | 0x051); /** installation parameters are wrong **/
    public final static short SW_STAT_INVALID_KEY_LENGTH =          (short)(ERROR_CLASS_WRONGDATA | 0x053); /** UO format error during de-serialize **/
    public final static short SW_EXTAPDU_TOO_LONG =                 (short)(ERROR_CLASS_WRONGDATA | 0x054); /** incoming command is too long **/
    public final static short SW_PROCESS_DATA_ALGORITHM_MISMATCH =  (short)(ERROR_CLASS_WRONGDATA | 0x055); /** formatting of processdata request - plaintext v encrypted UOID **/
    public final static short SW_HMAC_INVALID_LENGTH =              (short)(ERROR_CLASS_WRONGDATA | 0x057); /** HOTP verify - internal error? **/
    public final static short SW_INVALID_SE_HANDLE =                (short)(ERROR_CLASS_WRONGDATA | 0x060); /** pool SE commands - NOt USED? **/
    public final static short SW_AUTHMETHOD_INVALID_LENGTH =        (short)(ERROR_CLASS_WRONGDATA | 0x062); /** auth context parsing error **/
    public final static short SW_INCORRECT_SYSTEM_FOR_USEROBJECT =  (short)(ERROR_CLASS_WRONGDATA | 0x067); /** mix-up of production and test UO - e.g. **/
    public final static short SW_STAT_INVALID_APIKEY =              (short)(ERROR_CLASS_WRONGDATA | 0x068); /** not from SE **/
    public final static short SW_STAT_INVALID_CLIENT =              (short)(ERROR_CLASS_WRONGDATA | 0x069); /** not from SE **/
    public final static short SW_STAT_INVALID_USAGES_REQUIRED =     (short)(ERROR_CLASS_WRONGDATA | 0x072); /** more than 2^15 or 0 **/
    public static final short SW_STAT_NO_USER_OBJECT_TYPE =         (short)(ERROR_CLASS_WRONGDATA | 0x083); /** not from SE - check of requested operation **/
    public final static short SW_APP_DATA_LENGTH_WRONG =            (short)(ERROR_CLASS_WRONGDATA | 0x06a);
    public final static short SW_HOTP_INVALID_NUM_DIGITS =          (short)(ERROR_CLASS_WRONGDATA | 0x0b4); /* HOTP configuration error - allowed values 6-8 */
    public final static short SW_UNWRAP_UOKEYS_FAILED =             (short)(ERROR_CLASS_WRONGDATA | 0x0ad); /* UO re-wrapping - critical error */
    public final static short SW_UNWRAP_PROCESSDATA_FAILED =        (short)(ERROR_CLASS_WRONGDATA | 0x0af); /* UOID check during tokenize and processdata */
    public final static short SW_WRONG_BILLING_INFO_SEID =          (short)(ERROR_CLASS_WRONGDATA | 0x0b8); /* wrong SEID when requesting biling info */
    public final static short SW_AUTHMETHOD_NOT_ALLOWED =           (short)(ERROR_CLASS_WRONGDATA | 0x0b9); /* spawn SE authentication not implemented */
    public final static short SW_SHARE_INVALID_LENGTH =             (short)(ERROR_CLASS_WRONGDATA | 0x08d);
    public final static short SW_SHARE_INVALID_INDEX =              (short)(ERROR_CLASS_WRONGDATA | 0x08e);
    public final static short SW_SHARE_ALREADY_SET =                (short)(ERROR_CLASS_WRONGDATA | 0x08f);
    public final static short SW_SHARE_NOT_SET =                    (short)(ERROR_CLASS_WRONGDATA | 0x090);




    // ERROR_CLASS_SYNC - possibly marge with INFO?
    public final static short SW_STAT_USER_OBJECT_SLOT_INVALID =    (short)(ERROR_CLASS_SYNC | 0x046); /** external context has invalid pointer into internal slot **/
    public final static short SW_STAT_TOO_MANY_USAGES =             (short)(ERROR_CLASS_SYNC | 0x070); /** maxs are configurable through MAX_USER_OBJECTS_xxxx_SE **/
    public final static short SW_STAT_USER_OBJECT_ALREADY_EXISTS =  (short)(ERROR_CLASS_SYNC | 0x078); /** UO already exists in the SE where set **/
    public final static short SW_STAT_NO_USER_OBJECT_ID =           (short)(ERROR_CLASS_SYNC | 0x079); /** not from SE **/
    public final static short SW_STAT_SEEDSE_EXISTS =               (short)(ERROR_CLASS_SYNC | 0x07a); /** not from SE **/
    public final static short SW_SC_ALREADY_EXISTS =                (short)(ERROR_CLASS_SYNC | 0x07b); /** secure channel already open **/
    public final static short SW_SC_DOES_NOT_EXIST =                (short)(ERROR_CLASS_SYNC | 0x07c); /** secure channel not opened **/
    public final static short SW_SE_SECRETS_NOT_INITIALIZED =       (short)(ERROR_CLASS_SYNC | 0x07d); /** SC not in operational state **/
    public final static short SW_SE_SECRETS_ALREADY_INITIALIZED =   (short)(ERROR_CLASS_SYNC | 0x07e); /** SC already operational **/
    public final static short SW_STAT_SE_KEY_ALREADY_GENERATED =    (short)(ERROR_CLASS_SYNC | 0x07f); /** SE ID key pair already generated **/
    public final static short SW_PUBLIC_KEY_NOT_AVAILABLE_YET =     (short)(ERROR_CLASS_SYNC | 0x080); /** export of ID public key before it's available **/
    public final static short SW_INCORRECT_APPLET_STATE =           (short)(ERROR_CLASS_SYNC | 0x081); /** mostly that SE is not operational OR when key not generated **/
    public static final short SW_STAT_ENROLSE_EXISTS =              (short)(ERROR_CLASS_SYNC | 0x082); /** not from SE **/
    public static final short SW_NO_BILLING_INFO =                  (short)(ERROR_CLASS_SYNC | 0x084); /** no UO on SE -&gt; no billing info **/
    public final static short SW_COUNTER_OVERFLOW =                 (short)(ERROR_CLASS_SYNC | 0x085); /** billing info counter **/
    public final static short SW_STAT_SEEDSE_NOT_EXISTS =           (short)(ERROR_CLASS_SYNC | 0x087); /** not from SE **/
    public final static short SW_STAT_NO_APIKEY =                   (short)(ERROR_CLASS_SYNC | 0x088); /** not from SE **/
    public final static short SW_STAT_NO_CLIENT =                   (short)(ERROR_CLASS_SYNC | 0x089); /** not from SE **/
    public final static short SW_STAT_ALREADY_SET =                 (short)(ERROR_CLASS_SYNC | 0x08a); /** not from SE **/
    public final static short SW_STAT_REMOVED =                     (short)(ERROR_CLASS_SYNC | 0x08b); /** not from SE **/


    // ERROR_CLASS_SECURITY - a packet received from outside is
    public static final short SW_STAT_UNAUTHORIZED =                (short)(ERROR_CLASS_SECURITY | 0x0bc); /** not from SE **/


    // Secure channel-related errors
    public final static short SW_SCCTX_WRONG_MAC =                  (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0a3); /** MAC on SCCTX incorrect */
    public final static short SW_SCCTX_WRONG_FRESHNONCE =           (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0a4); /** fresness incorrect - SCCTX */
    public final static short SW_SCCTX_WRONG_TARGETSEID =           (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0a5); /** init update or close SC */
    public final static short SW_SCCTX_NONCE_OVERFLOW =             (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0a6); /** overflow of messages protected with SC */
    public final static short SW_SCCTX_WRONG_TARGET_AFTER_INITUP =  (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0a8); /** wrong target in SC setup */
    public final static short SW_SCCTX_NONCE_OVERFLOW_FORCED =      (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0ae); /** SC must be re-created */
    public final static short SW_SCCTX_WRONG_LENGTH =               (short)(ERROR_CLASS_SECURE_CHANNEL | 0x03f); /** again, error deserializing SC CTX **/
    public final static short SW_SCCTX_WRONG_LENGTH_INITUPDATE =    (short)(ERROR_CLASS_SECURE_CHANNEL | 0x040); /** init update - wrong data **/
    public final static short SW_SCCTX_WRONG_LENGTH_EXTAUTH =       (short)(ERROR_CLASS_SECURE_CHANNEL | 0x042); /** ext auth - message length is wrong **/
    public final static short SW_SCCTX_WRONG_TARGET_SCCTX_SLOT =    (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0a9); /** no SCCTX slot found on sending SE */
    public final static short SW_SCCTX_WRONG_TARGETSE_SCCTX =       (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0aa); /** response serialization - no SCCTX found */
    public final static short SW_SCCTX_WRONG_TARGET_AUTH_UNLOCK =   (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0a7); /** wrong target during unlocking with auth SE */
    public final static short SW_SCCTX_WRONG_TARGETSE_DESERIAL =    (short)(ERROR_CLASS_SECURE_CHANNEL | 0x0ab); /** target SE ID in encrypted message different from address */


    // External
    public final static short SW_HOTP_KEY_WRONG_LENGTH =            (short)(ERROR_CLASS_SECURITY_USER | 0x056); /** length of HOTP key wrong - must be between 16 and 64B **/
    public final static short SW_PASSWD_TOO_MANY_FAILED_TRIES =     (short)(ERROR_CLASS_SECURITY_USER | 0x063); /** authentication failure **/
    public final static short SW_PASSWD_INVALID_LENGTH =            (short)(ERROR_CLASS_SECURITY_USER | 0x064); /** authentication failure **/
    public final static short SW_WRONG_PASSWD =                     (short)(ERROR_CLASS_SECURITY_USER | 0x065); /** authentication failure **/
    public final static short SW_HOTP_TOO_MANY_FAILED_TRIES =       (short)(ERROR_CLASS_SECURITY_USER | 0x066); /** authentication failure **/
    public final static short SW_WRONG_MAC_DATA =                   (short)(ERROR_CLASS_SECURITY_USER | 0x0a1); /** MAC on data in processData **/
    public final static short SW_HOTP_WRONG_CODE =                  (short)(ERROR_CLASS_SECURITY_USER | 0x0b0); /** failed authentication */
    public final static short SW_AUTH_TOO_MANY_FAILED_TRIES =       (short)(ERROR_CLASS_SECURITY_USER | 0x0b1); /** failed authentication */
    public final static short SW_HOTP_COUNTER_OVERFLOW =            (short)(ERROR_CLASS_SECURITY_USER | 0x0b3); /** failed authentication */
    public final static short SW_AUTH_MISMATCH_USER_ID =            (short)(ERROR_CLASS_SECURITY_USER | 0x0b6); /** user authentication failure */
    public final static short SW_AUTHMETHOD_UNKNOWN =               (short)(ERROR_CLASS_SECURITY_USER | 0x0ba); /** user authentication method not implemented */


    // ERROR_CLASS_INFO
    public final static short SW_SCCTX_FRESHNESS_NONCE_ARRAY_FULL = (short)(ERROR_CLASS_INFO | 0x00b); /** too many sec. channels opened - small state array **/
    public final static short SW_SC_ALL_SCCTX_USED =                (short)(ERROR_CLASS_INFO | 0x00d); /** request for new SC ctx unsuccessful - all states used up **/
    public final static short SW_FILE_OPEN_FILE =                   (short)(ERROR_CLASS_INFO | 0x04b); /** not from SE **/
    public final static short SW_SPAWN_SE_LOCKED =                  (short)(ERROR_CLASS_INFO | 0x0b5); /** spawn SE is locked */
    public final static short SW_STAT_CAPACITY_OBJECTS =            (short)(ERROR_CLASS_INFO | 0x005); /** never from SC - max no of UO reached **/
    public final static short SW_STAT_TIMEOUT =                     (short)(ERROR_CLASS_INFO | 0x006); /** never from SC  - request timeout **/
    public final static short SW_STAT_SE_UNRECOGNIZED =             (short)(ERROR_CLASS_INFO | 0x008); /** never from SC  - booting - unrecognized SE **/
    public final static short SW_STAT_ERR_THREADPOOL_SHUTDOWN =     (short)(ERROR_CLASS_INFO | 0x00f); /** not used anymore? never from SC - no more requests to be accepted - shutdown **/
    public final static short SW_INVALID_STATE_FLAG =               (short)(ERROR_CLASS_INFO | 0x04d); /** SE not in operational state **/
    public final static short SW_STAT_USAGE_LEFT_00 =               (short)(ERROR_CLASS_INFO | 0x0d0); /** usage counter at 0 */
    public final static short SW_STAT_LOCKING_ERROR =               (short)(ERROR_CLASS_INFO | 0x0d1); /** interruption during SC establishment */
    public final static short SW_STAT_NO_FREE_UO_SLOT =             (short)(ERROR_CLASS_INFO | 0x075); /** no free slot for a new UO on SE */
    public final static short SW_STAT_BILLING_INFO_NOT_READ_OUT =   (short)(ERROR_CLASS_INFO | 0x074); /** no billing info to read */
    public final static short SW_STAT_TOO_MANY_USAGES_SUM =         (short)(ERROR_CLASS_INFO | 0x073); /** SE receives too much and it breaches maximum when summed with existing credits */


    //ERROR_CLASS_OTHER_SE
    public final static short SW_SC_ERROR_SLAVE_SE =              (short)(ERROR_CLASS_OTHER_SE | 0x0f0);
    public static final short SW_SC_ERROR_MASTER_SE =             (short)(ERROR_CLASS_OTHER_SE | 0x0f1);
}
