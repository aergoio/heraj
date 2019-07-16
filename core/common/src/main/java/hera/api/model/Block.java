/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class Block {

  @NonNull
  @Default
  BlockHash hash = BlockHash.of(BytesValue.EMPTY);

  @NonNull
  @Default
  BlockHeader blockHeader = BlockHeader.newBuilder().build();

  @NonNull
  @Default
  List<Transaction> transactions = emptyList();

  Block(final BlockHash blockHash, final BlockHeader blockHeader,
      final List<Transaction> transactions) {
    assertNotNull(blockHash, "The blockHash must not null");
    assertNotNull(blockHeader, "The blockHeader must not null");
    assertNotNull(transactions, "The transactions must not null");
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
