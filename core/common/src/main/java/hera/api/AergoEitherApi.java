/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface AergoEitherApi {

  /**
   * Get account either operation.
   *
   * @return {@code AccountEitherOperation}
   */
  AccountEitherOperation getAccountEitherOperation();

  /**
   * Get keystore either operation.
   *
   * @return {@code KeyStoreEitherOperation}
   */
  KeyStoreEitherOperation getKeyStoreEitherOperation();

  /**
   * Get block either operation.
   *
   * @return {@code BlockEitherOperation}
   */
  BlockEitherOperation getBlockEitherOperation();

  /**
   * Get blockchain either operation.
   *
   * @return {@code BlockchainEitherOperation}
   */
  BlockchainEitherOperation getBlockchainEitherOperation();

  /**
   * Get transaction either operation.
   *
   * @return {@code TransactionEitherOperation}
   */
  TransactionEitherOperation getTransactionEitherOperation();

  /**
   * Get contract either operation.
   *
   * @return {@code ContractEitherOperation}
   */
  ContractEitherOperation getContractEitherOperation();

}
