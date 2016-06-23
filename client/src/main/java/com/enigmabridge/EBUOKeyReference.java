package com.enigmabridge;

/**
 * Interface which references to a single EB UO key.
 * Created by dusanklinec on 26.04.16.
 */
public interface EBUOKeyReference extends EBUOReference{
    /**
     * Returns EB key reference
     * @return
     */
    UserObjectKey getUserObjectKey();
}
