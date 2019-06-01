/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class Block {

  @Getter
  protected final BlockHash hash;

  @Getter
  protected final BlockHeader blockHeader;

  @Getter
  protected final List<Transaction> transactions;

  /**
   * Block constructor.
   *
   * @param blockHash a block hash
   * @param blockHeader a block header
   * @param transactions transactions in block
   */
  @ApiAudience.Private
  public Block(final BlockHash blockHash, final BlockHeader blockHeader,
      final List<Transaction> transactions) {
    this.hash = blockHash;
    this.blockHeader = blockHeader;
    this.transactions = unmodifiableList(transactions);
  }

  public BytesValue getChainId() {
    return blockHeader.getChainId();
  }

  public BlockHash getPreviousHash() {
    return blockHeader.getPreviousHash();
  }

  public long getBlockNumber() {
    return blockHeader.getBlockNumber();
  }

  public long getTimestamp() {
    return blockHeader.getTimestamp();
  }

  public Hash getRootHash() {
    return blockHeader.getRootHash();
  }

  public Hash getTxRootHash() {
    return blockHeader.getTxRootHash();
  }

  public Hash getReceiptRootHash() {
    return blockHeader.getReceiptRootHash();
  }

  public long getConfirmsCount() {
    return blockHeader.getConfirmsCount();
  }

  public BytesValue getPublicKey() {
    return blockHeader.getPublicKey();
  }

  public AccountAddress getCoinbaseAccount() {
    return blockHeader.getCoinbaseAccount();
  }

  public Signature getSign() {
    return blockHeader.getSign();
  }

}
