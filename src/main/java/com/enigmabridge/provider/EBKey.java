package com.enigmabridge.provider;

import sun.security.util.Length;

import java.security.Key;

/**
 * Group for all EB keys.
 * Conforms to the general Java Key so it can be used in the Crypto provider calls
 * such as:
 *   Cipher c = Cipher.getInstance("RSA");
 *   c.init(Cipher.ENCRYPT_MODE, ebKey);
 *
 * Cipher make use of a delayed provider selection as specified in
 * http://docs.oracle.com/javase/7/docs/technotes/guides/security/p11guide.html
 *
 * When EBKey is detected in init(), other ciphers throw an InvalidKeyException. Our CipherSpi implementation
 * registered by our provider accepts this key. The key is usually the only one argument passed to the cipher
 * so it usually contains all required information for using EB UserObjects.
 *
 * Created by dusanklinec on 26.04.16.
 */
public interface EBKey extends Key, Length {

}
