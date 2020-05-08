/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.NumberUtils.positiveToByteArray;
import static hera.util.Sha256Utils.digest;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction.TxType;
import hera.api.transaction.CreateNameTransactionBuilder;
import hera.api.transaction.DeployContractTransactionBuilder;
import hera.api.transaction.InvokeContractTransactionBuilder;
import hera.api.transaction.PlainTransactionBuilder;
import hera.api.transaction.ReDeployContractTransactionBuilder;
import hera.api.transaction.StakeTransactionBuilder;
import hera.api.transaction.UnstakeTransactionBuilder;
import hera.api.transaction.UpdateNameTransactionBuilder;
import hera.api.transaction.VoteTransactionBuilder;
import hera.api.transaction.dsl.CreateNameTransaction;
import hera.api.transaction.dsl.DeployContractTransaction;
import hera.api.transaction.dsl.InvokeContractTransaction;
import hera.api.transaction.dsl.PlainTransaction;
import hera.api.transaction.dsl.ReDeployContractTransaction;
import hera.api.transaction.dsl.StakeTransaction;
import hera.api.transaction.dsl.UnstakeTransaction;
import hera.api.transaction.dsl.UpdateNameTransaction;
import hera.api.transaction.dsl.VoteTransaction;
import hera.exception.HerajException;
import hera.util.LittleEndianDataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
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
  protected final ChainIdHash chainIdHash;

  @NonNull
  protected final AccountAddress sender;

  @NonNull
  protected final AccountAddress recipient;

  @NonNull
  protected final Aer amount;

  protected final long nonce;

  @NonNull
  protected final Fee fee;

  @NonNull
  protected final BytesValue payload;

  @NonNull
  protected final TxType txType;

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

  /**
   * Calculate a hash of transaction.
   *
   * @return a hash of transaction
   */
  public TxHash calculateHash() {
    try {
      final ByteArrayOutputStream raw = new ByteArrayOutputStream();
      final LittleEndianDataOutputStream dataOut = makeStream(raw);
      dataOut.flush();
      dataOut.close();
      final byte[] digested = digest(raw.toByteArray());
      return TxHash.of(BytesValue.of(digested));
    } catch (final IOException e) {
      throw new HerajException(e);
    }
  }

  /**
   * Calculate a hash of transaction.
   *
   * @param signature a signature
   * @return a hash of transaction
   */
  public TxHash calculateHash(final Signature signature) {
    try {
      final ByteArrayOutputStream raw = new ByteArrayOutputStream();
      final LittleEndianDataOutputStream dataOut = makeStream(raw);
      dataOut.write(signature.getSign().getValue());
      dataOut.flush();
      dataOut.close();
      final byte[] digested = digest(raw.toByteArray());
      return TxHash.of(BytesValue.of(digested));
    } catch (final IOException e) {
      throw new HerajException(e);
    }
  }

  protected LittleEndianDataOutputStream makeStream(final ByteArrayOutputStream raw)
      throws IOException {
    final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(raw);
    // WARNING : follow the stream order with server
    dataOut.writeLong(getNonce());
    dataOut.write(getSender().getBytesValue().getValue());
    dataOut.write(getRecipient().getBytesValue().getValue());
    dataOut.write(positiveToByteArray(getAmount().getValue()));
    dataOut.write(getPayload().getValue());
    dataOut.writeLong(getFee().getLimit());
    dataOut.write(positiveToByteArray(getFee().getPrice().getValue()));
    dataOut.writeInt(getTxType().getIntValue());
    dataOut.write(getChainIdHash().getBytesValue().getValue());
    return dataOut;
  }

}
