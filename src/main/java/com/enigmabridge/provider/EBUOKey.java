package com.enigmabridge.provider;

import com.enigmabridge.EBEngineReference;
import com.enigmabridge.EBOperationConfigurationReference;
import com.enigmabridge.UserObjectKey;
import com.enigmabridge.provider.EBKey;

/**
 * EB key represented with single UO.
 * Conforms to general Java Key, as a parent interface EBKey.
 *
 * This interface moreover guarantees UserObject information access so
 * required key operation can be called on the EB + additional configuration.
 *
 * Created by dusanklinec on 26.04.16.
 */
public interface EBUOKey extends EBKey, EBEngineReference, EBOperationConfigurationReference, UserObjectKey {

}
