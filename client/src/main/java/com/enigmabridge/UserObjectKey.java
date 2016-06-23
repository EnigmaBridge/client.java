package com.enigmabridge;

import sun.security.util.Length;

/**
 * Interface for EB based keys.
 *
 * Created by dusanklinec on 26.04.16.
 */
public interface UserObjectKey extends EBUOKeyReference, UserObjectInfo, Length{
    /**
     * Returns algorithm the key is dedicated to.
     * @return algorithm identification
     */
    String getAlgorithm();

    /**
     * Type of the key. (private/public/secret=symmetric).
     * @return key type.
     */
    UserObjectKeyType getKeyType();
}
