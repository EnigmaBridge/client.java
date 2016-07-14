package com.enigmabridge;

/**
 * Wrapper for EB operation configuration.
 * Class can contain e.g. timeouts, retry counts, misc EB settings for working with EB UOs.
 *
 * This configuration is related to particular operation with UO and is request specific.
 *
 * Created by dusanklinec on 27.04.16.
 */
public class EBOperationConfiguration {
    public EBOperationConfiguration copy(){
        return this;
    }
}
