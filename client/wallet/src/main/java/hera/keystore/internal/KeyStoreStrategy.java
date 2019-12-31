/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore.internal;

import com.fasterxml.jackson.databind.JsonNode;
import hera.key.AergoKey;

public interface KeyStoreStrategy {

  String encrypt(AergoKey key, char[] password);

  AergoKey decrypt(String json, char[] password);

  AergoKey decrypt(JsonNode json, char[] password);

}
