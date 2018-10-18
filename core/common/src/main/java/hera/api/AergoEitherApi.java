/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface AergoEitherApi {
  AccountEitherOperation getAccountEitherOperation();

  BlockEitherOperation getBlockEitherOperation();

  BlockchainEitherOperation getBlockchainEitherOperation();

  TransactionEitherOperation getTransactionEitherOperation();

  ContractEitherOperation getContractEitherOperation();
}
