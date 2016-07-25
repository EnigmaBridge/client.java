package com.enigmabridge;

import java.io.Serializable;

/**
 * API for UserObject (UO) Info.
 * Provides information to make use of EB user objects.
 *
 * Created by dusanklinec on 26.04.16.
 */
public interface UserObjectInfo extends EBSettings, Serializable {
    /**
     * Returns User object handle / ID.
     * @return UO ID
     */
     long getUoid();

    /**
     * Returns user object type.
     * Required for API Token generation.
     * @return UO type
     */
    UserObjectType getUserObjectType();

     /**
      * Communication encryption &amp; MAC keys.
      * UO-specific. Strictly required for UO use.
      * @return EBCommKeys
      */
     EBCommKeys getCommKeys();
}

