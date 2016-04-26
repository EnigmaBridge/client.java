package com.enigmabridge;

/**
 * Interface which references to a single EB UO.
 * Created by dusanklinec on 26.04.16.
 */
public interface EBUOReference {
    /**
     * Returns user object related to this key.
     * @return
     */
    UserObjectInfo getUserObjectInfo();
}
