/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction.TxType;
import hera.transaction.CreateNameTransactionBuilder;
import hera.transaction.DeployContractTransactionBuilder;
import hera.transaction.InvokeContractTransactionBuilder;
import hera.transaction.PlainTransactionBuilder;
import hera.transaction.ReDeployContractTransactionBuilder;
import hera.transaction.StakeTransactionBuilder;
import hera.transaction.UnstakeTransactionBuilder;
import hera.transaction.UpdateNameTransactionBuilder;
import hera.transaction.VoteTransactionBuilder;
import hera.transaction.dsl.CreateNameTransaction;
import hera.transaction.dsl.DeployContractTransaction;
import hera.transaction.dsl.InvokeContractTransaction;
import hera.transaction.dsl.PlainTransaction;
import hera.transaction.dsl.ReDeployContractTransaction;
import hera.transaction.dsl.StakeTransaction;
import hera.transaction.dsl.UnstakeTransaction;
import hera.transaction.dsl.UpdateNameTransaction;
import hera.transaction.dsl.VoteTransaction;
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
