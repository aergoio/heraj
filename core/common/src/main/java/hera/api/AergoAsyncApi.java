/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface AergoAsyncApi {
  AccountAsyncOperation getAccountAsyncOperation();

  BlockAsyncOperation getBlockAsyncOperation();

  BlockChainAsyncOperation getBlockChainAsyncOperation();

  TransactionAsyncOperation getTransactionAsyncOperation();

  ContractAsyncOperation getContractAsyncOperation();
}
