package com.enigmabridge;

import java.io.Serializable;

/**
 * Created by dusanklinec on 26.04.16.
 */
public interface UserObjectInfo extends Serializable {
     long getUoid();
     String getApiKey();
     EBCommKeys getCommKeys();
     long getUserObjectType();
     EBEndpointInfo getEndpointInfo();
}
