/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AergoApi;
import hera.api.model.ChainIdHash;
import java.io.Closeable;

@ApiAudience.Public
@ApiStability.Unstable
public interface AergoClient extends AergoApi, Closeable {

  /**
   * Get cached chain id hash. null if no cached one.
   *
   * @return a chain id hash
   */
  ChainIdHash getCachedChainIdHash();

  /**
   * Cache chain id hash.
   *
   * @param chainIdHash a chain id hash
   */
  void cacheChainIdHash(ChainIdHash chainIdHash);

  void close();

}
