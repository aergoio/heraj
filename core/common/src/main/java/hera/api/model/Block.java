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

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode(callSuper = true)
public class Block extends BlockHeader {

  @Getter
  protected final List<Transaction> transactions;

  /**
   * Block constructor.
   *
   * @param blockHeader a block header
   * @param transactions transactions of block
   */
  @ApiAudience.Private
  public Block(final BlockHeader blockHeader, final List<Transaction> transactions) {
    this(blockHeader.getChainId(), blockHeader.getHash(), blockHeader.getPreviousHash(),
        blockHeader.getBlockNumber(), blockHeader.getTimestamp(), blockHeader.getRootHash(),
        blockHeader.getTxRootHash(), blockHeader.getReceiptRootHash(),
        blockHeader.getConfirmsCount(), blockHeader.getPublicKey(), blockHeader.getSign(),
        blockHeader.getCoinbaseAccount(), blockHeader.getTxCount(), transactions);
  }

  /**
   * Block constructor.
   *
   * @param chainId a chain id
   * @param hash a block hash
   * @param previousHash a previous block hash
   * @param blockNumber a block number
   * @param timestamp a timestamp
   * @param rootHash a block root hash
   * @param txRootHash a tx root hash
   * @param receiptRootHash a receipt root hash
   * @param confirmsCount confirmsCount
   * @param publicKey a public key
   * @param sign a sign
   * @param coinbaseAccount a coinbase account
   * @param txCount a transaction count
   * @param transactions transactions of block
   */
  @ApiAudience.Private
  public Block(final Hash chainId, final BlockHash hash, final BlockHash previousHash,
      final long blockNumber, final long timestamp, final BlockHash rootHash,
      final TxHash txRootHash, final Hash receiptRootHash, final long confirmsCount,
      final Hash publicKey, final Hash sign, final AccountAddress coinbaseAccount,
      final long txCount, final List<Transaction> transactions) {
    super(chainId, hash, previousHash, blockNumber, timestamp, rootHash, txRootHash,
        receiptRootHash, confirmsCount, publicKey, sign, coinbaseAccount, txCount);
    this.transactions = unmodifiableList(transactions);
  }

  @Override
  public String toString() {
    return String.format("Block(%s, transactions=%s)", super.toString(), transactions.toString());
  }

}
