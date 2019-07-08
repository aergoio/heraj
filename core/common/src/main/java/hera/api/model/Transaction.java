/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString(callSuper = true)
@EqualsAndHashCode
public class Transaction {

  @Getter
  protected final RawTransaction rawTransaction;

  @Getter
  protected final Signature signature;

  @Getter
  protected final TxHash hash;

  @Getter
  protected final BlockHash blockHash;

  @Getter
  protected final int indexInBlock;

  @Getter
  protected final boolean confirmed;

  @RequiredArgsConstructor
  public enum TxType {
    UNRECOGNIZED(-1), NORMAL(0), GOVERNANCE(1), REDEPLOY(2);

    @Getter
    private final int intValue;
  }

  /**
   * Transaction constructor.
   *
   * @param rawTransaction a raw transaction
   * @param signature a signature
   * @param txHash a tx hash
   */
  @ApiAudience.Private
  public Transaction(final RawTransaction rawTransaction, final Signature signature,
      final TxHash txHash) {
    this(rawTransaction, signature, txHash, new BlockHash(BytesValue.EMPTY), 0, false);
  }

  /**
   * Transaction constructor.
   *
   * @param rawTransaction a raw transaction
   * @param signature a signature
   * @param txHash a txHash
   * @param blockHash a block hash
   * @param indexInBlock an indexInBlock
   * @param isConfirmed an confirm state
   */
  @ApiAudience.Private
  public Transaction(final RawTransaction rawTransaction, final Signature signature,
      final TxHash txHash, final BlockHash blockHash, final int indexInBlock,
      final boolean isConfirmed) {
    assertNotNull(rawTransaction, "Raw transaction must not null");
    this.rawTransaction = rawTransaction;
    this.signature = null != signature ? signature : Signature.of(BytesValue.EMPTY);
    this.hash = null != txHash ? txHash : TxHash.of(BytesValue.EMPTY);
    this.blockHash = null != blockHash ? blockHash : BlockHash.of(BytesValue.EMPTY);
    this.indexInBlock = indexInBlock;
    this.confirmed = isConfirmed;
  }

  public ChainIdHash getChainIdHash() {
    return rawTransaction.getChainIdHash();
  }

  public AccountAddress getSender() {
    return rawTransaction.getSender();
  }

  public AccountAddress getRecipient() {
    return rawTransaction.getRecipient();
  }

  public Aer getAmount() {
    return rawTransaction.getAmount();
  }

  public Long getNonce() {
    return rawTransaction.getNonce();
  }

  public Fee getFee() {
    return rawTransaction.getFee();
  }

  public BytesValue getPayload() {
    return rawTransaction.getPayload();
  }

  public TxType getTxType() {
    return rawTransaction.getTxType();
  }

}
