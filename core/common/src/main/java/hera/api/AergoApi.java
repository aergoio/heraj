/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface AergoApi {

  /**
   * Get account operation.
   *
   * @return {@code AccountOperation}
   */
  AccountOperation getAccountOperation();

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
