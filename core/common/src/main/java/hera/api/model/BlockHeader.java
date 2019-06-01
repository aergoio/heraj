/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class BlockHeader {

  @Getter
  protected final BytesValue chainId;

  @Getter
  protected final BlockHash previousHash;

  @Getter
  protected final long blockNumber;

  @Getter
  protected final long timestamp;

  @Getter
  protected final Hash rootHash;

  @Getter
  protected final Hash txRootHash;

  @Getter
  protected final Hash receiptRootHash;

  @Getter
  protected final long confirmsCount;

  @Getter
  protected final BytesValue publicKey;

  @Getter
  protected final AccountAddress coinbaseAccount;

  @Getter
  protected final Signature sign;

  /**
   * BlockHeader constructor.
   *
   * @param chainId a chain identifier
   * @param previousHash a hash of previous block
   * @param blockNumber a block number
   * @param timestamp a creation time stamp
   * @param rootHash a hash of root of block merkle tree
   * @param txRootHash a hash of root of transaction merkle tree
   * @param receiptRootHash a number of blocks this block is able to confirm
   * @param confirmsCount a number of blocks this block is able to confirm
   * @param publicKey a block producer's public key
   * @param coinbaseAccount a address of account to receive fees
   * @param sign a block producer's signature of BlockHeader
   */
  @ApiAudience.Private
  public BlockHeader(final BytesValue chainId, final BlockHash previousHash, final long blockNumber,
      final long timestamp, final Hash rootHash, final Hash txRootHash, final Hash receiptRootHash,
      final long confirmsCount, final BytesValue publicKey, final AccountAddress coinbaseAccount,
      final Signature sign) {
    this.chainId = null != chainId ? chainId : BytesValue.EMPTY;
    this.previousHash = null != previousHash ? previousHash : BlockHash.of(BytesValue.EMPTY);
    this.blockNumber = blockNumber;
    this.timestamp = timestamp;
    this.rootHash = null != rootHash ? rootHash : Hash.of(BytesValue.EMPTY);
    this.txRootHash = null != txRootHash ? txRootHash : Hash.of(BytesValue.EMPTY);
    this.receiptRootHash = null != receiptRootHash ? receiptRootHash : Hash.of(BytesValue.EMPTY);
    this.confirmsCount = confirmsCount;
    this.publicKey = null != publicKey ? publicKey : BytesValue.EMPTY;
    this.coinbaseAccount =
        null != coinbaseAccount ? coinbaseAccount : AccountAddress.of(BytesValue.EMPTY);
    this.sign = null != sign ? sign : Signature.of(BytesValue.EMPTY);
  }

}
