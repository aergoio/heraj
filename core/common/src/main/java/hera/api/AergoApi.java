/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ChainIdHash;

@ApiAudience.Public
@ApiStability.Unstable
public interface AergoApi {

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

  /**
   * Get account operation.
   *
   * @return {@code AccountOperation}
   */
  AccountOperation getAccountOperation();

  /**
   * Get keystore operation.
   *
   * @return {@code KeyStoreOperation}
   */
  KeyStoreOperation getKeyStoreOperation();

  /**
   * Get block operation.
   *
   * @return {@code BlockOperation}
   */
  BlockOperation getBlockOperation();

  /**
   * Get blockchain operation.
   *
   * @return {@code BlockchainOperation}
   */
  BlockchainOperation getBlockchainOperation();

  /**
   * Get transaction operation.
   *
   * @return {@code TransactionOperation}
   */
  TransactionOperation getTransactionOperation();

  /**
   * Get contract operation.
   *
   * @return {@code ContractOperation}
   */
  ContractOperation getContractOperation();

}
