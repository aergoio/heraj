/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction.TxType;
import hera.transaction.CreateNameTransactionBuilder;
import hera.transaction.DeployContractTransactionBuilder;
import hera.transaction.InvokeContractTransactionBuilder;
import hera.transaction.PlainTransactionBuilder;
import hera.transaction.StakeTransactionBuilder;
import hera.transaction.UnstakeTransactionBuilder;
import hera.transaction.UpdateNameTransactionBuilder;
import hera.transaction.VoteTransactionBuilder;
import hera.transaction.dsl.CreateNameTransaction;
import hera.transaction.dsl.DeployContractTransaction;
import hera.transaction.dsl.InvokeContractTransaction;
import hera.transaction.dsl.PlainTransaction;
import hera.transaction.dsl.StakeTransaction;
import hera.transaction.dsl.UnstakeTransaction;
import hera.transaction.dsl.UpdateNameTransaction;
import hera.transaction.dsl.VoteTransaction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
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

  @Getter
  protected final ChainIdHash chainIdHash;

  @Getter
  protected final AccountAddress sender;

  @Getter
  protected final AccountAddress recipient;

  @Getter
  protected final Aer amount;

  @Getter
  protected final Long nonce;

  @Getter
  protected final Fee fee;

  @Getter
  protected final BytesValue payload;

  @Getter
  protected final TxType txType;

  /**
   * RawTransaction constructor.
   *
   * @param chainIdHash a chain id hash
   * @param sender a sender
   * @param recipient a recipient
   * @param amount an amount
   * @param nonce an nonce
   * @param fee a fee
   * @param payload a payload
   * @param txType a txType
   */
  @ApiAudience.Private
  public RawTransaction(final ChainIdHash chainIdHash, final AccountAddress sender,
      final AccountAddress recipient,
      final Aer amount, final Long nonce, final Fee fee, final BytesValue payload,
      final TxType txType) {
    assertNotNull(chainIdHash, "Chain id hash must not null");
    this.chainIdHash = chainIdHash;
    this.sender = null != sender ? sender : AccountAddress.of(BytesValue.EMPTY);
    this.recipient = null != recipient ? recipient : AccountAddress.of(BytesValue.EMPTY);
    this.amount = null != amount ? amount : Aer.EMPTY;
    this.nonce = nonce;
    this.fee = null != fee ? fee : Fee.EMPTY;
    this.payload = null != payload ? payload : BytesValue.EMPTY;
    this.txType = null != txType ? txType : TxType.UNRECOGNIZED;
  }

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
