/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Transaction extends RawTransaction {

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
    UNRECOGNIZED(-1), NORMAL(0), GOVERNANCE(1);

    @Getter
    private final int intValue;
  }

  /**
   * Transaction constructor.
   *
   * @param rawTransaction a transaction without block information
   * @param signature a signature
   * @param txHash a txHash
   * @param blockHash a block hash
   * @param indexInBlock an indexInBlock
   * @param isConfirmed an confirm state
   */
  public Transaction(final RawTransaction rawTransaction, final Signature signature,
      final TxHash txHash, final BlockHash blockHash, final int indexInBlock,
      final boolean isConfirmed) {
    this(rawTransaction.getSender(), rawTransaction.getRecipient(), rawTransaction.getAmount(),
        rawTransaction.getNonce(), rawTransaction.getFee(),
        rawTransaction.getPayload(), rawTransaction.getTxType(),
        signature, txHash, blockHash, indexInBlock, isConfirmed);
  }

  /**
   * Transaction constructor.
   *
   * @param sender a sender
   * @param recipient a recipient
   * @param amount an amount
   * @param nonce an nonce
   * @param fee a fee
   * @param payload a payload
   * @param txType a txType
   * @param signature a signature
   * @param txHash a txHash
   * @param blockHash a block hash
   * @param indexInBlock an indexInBlock
   * @param isConfirmed an confirm state
   */
  public Transaction(final AccountAddress sender, final AccountAddress recipient,
      final BigInteger amount, long nonce, final Fee fee, final BytesValue payload,
      final TxType txType, final Signature signature, final TxHash txHash,
      final BlockHash blockHash, final int indexInBlock, final boolean isConfirmed) {
    super(sender, recipient, amount, nonce, fee, payload, txType);
    this.signature = null != signature ? signature : Signature.of(BytesValue.EMPTY);
    this.hash = null != txHash ? txHash : TxHash.of(BytesValue.EMPTY);
    this.blockHash = null != blockHash ? blockHash : BlockHash.of(BytesValue.EMPTY);
    this.indexInBlock = indexInBlock;
    this.confirmed = isConfirmed;
  }

}
