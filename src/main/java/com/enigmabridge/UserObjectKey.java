package com.enigmabridge;

import sun.security.util.Length;

/**
 * Interface for EB based keys.
 * Created by dusanklinec on 26.04.16.
 */
public interface UserObjectKey extends UserObjectInfo, EBUOKeyReference, Length{
    String getAlgorithm();
    UserObjectKeyType getKeyType();
}
