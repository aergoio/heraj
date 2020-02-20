/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.key.AergoKey;

public interface KeyFormatStrategy {

  String encrypt(AergoKey key, char[] password);

  AergoKey decrypt(String json, char[] password);

}