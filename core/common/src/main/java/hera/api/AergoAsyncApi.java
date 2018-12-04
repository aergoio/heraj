/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface AergoAsyncApi {

  /**
   * Get account async operation.
   *
   * @return {@code AccountAsyncOperation}
   */
  AccountAsyncOperation getAccountAsyncOperation();

  /**
   * Get keystore async operation.
   *
   * @return {@code KeyStoreAsyncOperation}
   */
  KeyStoreAsyncOperation getKeyStoreAsyncOperation();

  /**
   * Get block async operation.
   *
   * @return {@code BlockAsyncOperation}
   */
  BlockAsyncOperation getBlockAsyncOperation();

  /**
   * Get blockchain async operation.
   *
   * @return {@code BlockchainAsyncOperation}
   */
  BlockchainAsyncOperation getBlockchainAsyncOperation();

  /**
   * Get transaction async operation.
   *
   * @return {@code TransactionAsyncOperation}
   */
  TransactionAsyncOperation getTransactionAsyncOperation();

  /**
   * Get contract async operation.
   *
   * @return {@code ContractAsyncOperation}
   */
  ContractAsyncOperation getContractAsyncOperation();

}
