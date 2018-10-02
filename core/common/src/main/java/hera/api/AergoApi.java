/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface AergoApi {
  AccountOperation getAccountOperation();

  SignOperation getSignOperation();

  BlockOperation getBlockOperation();

  BlockChainOperation getBlockChainOperation();

  TransactionOperation getTransactionOperation();

  ContractOperation getContractOperation();
}
