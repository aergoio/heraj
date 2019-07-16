/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class Event {

  @NonNull
  ContractAddress from;

  @NonNull
  @Default
  String name = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  List<Object> args = emptyList();

  int index;

  @NonNull
  @Default
  TxHash txHash = TxHash.of(BytesValue.EMPTY);

  int indexInBlock;

  @NonNull
  @Default
  BlockHash blockHash = BlockHash.of(BytesValue.EMPTY);

  long blockNumber;

  Event(final ContractAddress from, final String name, final List<Object> args,
      final int index, final TxHash txHash, int indexInBlock, final BlockHash blockHash,
      final long blockNumber) {
    assertNotNull(from, "Contract address must not null");
    assertNotNull(name, "Name must not null");
    assertNotNull(args, "Event args must not null");
    assertNotNull(txHash, "Event transaction hash must not null");
    assertNotNull(blockHash, "Event block hash must not null");
    this.from = from;
    this.name = name;
    this.args = unmodifiableList(args);
    this.index = index;
    this.txHash = txHash;
    this.indexInBlock = indexInBlock;
    this.blockHash = blockHash;
    this.blockNumber = blockNumber;
  }

}
