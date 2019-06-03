/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.ChainIdHash;

public interface ChainIdHashHolder {

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

}
