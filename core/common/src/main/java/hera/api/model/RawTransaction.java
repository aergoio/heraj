/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction.TxType;
import hera.spec.transaction.CreateNameTransactionBuilder;
import hera.spec.transaction.DeployContractTransactionBuilder;
import hera.spec.transaction.InvokeContractTransactionBuilder;
import hera.spec.transaction.PlainTransactionBuilder;
import hera.spec.transaction.ReDeployContractTransactionBuilder;
import hera.spec.transaction.StakeTransactionBuilder;
import hera.spec.transaction.UnstakeTransactionBuilder;
import hera.spec.transaction.UpdateNameTransactionBuilder;
import hera.spec.transaction.VoteTransactionBuilder;
import hera.spec.transaction.dsl.CreateNameTransaction;
import hera.spec.transaction.dsl.DeployContractTransaction;
import hera.spec.transaction.dsl.InvokeContractTransaction;
import hera.spec.transaction.dsl.PlainTransaction;
import hera.spec.transaction.dsl.ReDeployContractTransaction;
import hera.spec.transaction.dsl.StakeTransaction;
import hera.spec.transaction.dsl.UnstakeTransaction;
import hera.spec.transaction.dsl.UpdateNameTransaction;
import hera.spec.transaction.dsl.VoteTransaction;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
public class RawTransaction {

  @ApiAudience.Public
  public static PlainTransaction.WithNothing newBuilder() {
    return new PlainTransactionBuilder();
  }

  @ApiAudience.Public
  public static PlainTransaction.WithChainIdHash newBuilder(final ChainIdHash chainIdHash) {
    return new PlainTransactionBuilder().chainIdHash(chainIdHash);
  }

  @ApiAudience.Public
  public static DeployContractTransaction.WithNothing newDeployContractBuilder() {
    return new DeployContractTransactionBuilder();
  }

  @ApiAudience.Public
  public static ReDeployContractTransaction.WithNothing newReDeployContractBuilder() {
    return new ReDeployContractTransactionBuilder();
  }

  @ApiAudience.Public
  public static InvokeContractTransaction.WithNothing newInvokeContractBuilder() {
    return new InvokeContractTransactionBuilder();
  }

  @ApiAudience.Public
  public static CreateNameTransaction.WithNothing newCreateNameTxBuilder() {
    return new CreateNameTransactionBuilder();
  }

  @ApiAudience.Public
  public static UpdateNameTransaction.WithNothing newUpdateNameTxBuilder() {
    return new UpdateNameTransactionBuilder();
  }

  @ApiAudience.Public
  public static StakeTransaction.WithNothing newStakeTxBuilder() {
    return new StakeTransactionBuilder();
  }

  @ApiAudience.Public
  public static UnstakeTransaction.WithNothing newUnstakeTxBuilder() {
    return new UnstakeTransactionBuilder();
  }

  @ApiAudience.Public
  public static VoteTransaction.WithNothing newVoteTxBuilder() {
    return new VoteTransactionBuilder();
  }

  @NonNull
  ChainIdHash chainIdHash;

  @NonNull
  AccountAddress sender;

  @NonNull
  AccountAddress recipient;

  @NonNull
  Aer amount;

  long nonce;

  @NonNull
  Fee fee;

  @NonNull
  BytesValue payload;

  @NonNull
  TxType txType;


  // remove those withers when Wither of lombok is stable.

  /**
   * Return a {@code RawTransaction} with new chain id hash.
   *
   * @param chainIdHash a chain id hash
   * @return a {@code RawTransaction} instance
   */
  public RawTransaction withChainIdHash(final ChainIdHash chainIdHash) {
    return new RawTransaction(chainIdHash, sender, recipient, amount, nonce, fee, payload, txType);
  }

  /**
   * Return a {@code RawTransaction} with new nonce.
   *
   * @param nonce an new nonce
   * @return a {@code RawTransaction} instance
   */
  public RawTransaction withNonce(final long nonce) {
    return new RawTransaction(chainIdHash, sender, recipient, amount, nonce, fee, payload, txType);
  }

}
