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

  SignAsyncOperation getSignAsyncOperation();

  BlockAsyncOperation getBlockAsyncOperation();

  BlockChainAsyncOperation getBlockChainAsyncOperation();

  TransactionAsyncOperation getTransactionAsyncOperation();

  ContractAsyncOperation getContractAsyncOperation();
}
