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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@Getter
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class Event {

  @NonNull
  @Default
  protected final ContractAddress from = ContractAddress.EMPTY;

  @NonNull
  @Default
  protected final String name = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  protected final List<Object> args = unmodifiableList(emptyList());

  @Default
  protected final int index = 0;

  @NonNull
  @Default
  protected final TxHash txHash = TxHash.EMPTY;

  @Default
  protected final int indexInBlock = 0;

  @NonNull
  @Default
  protected final BlockHash blockHash = BlockHash.EMPTY;

  @Default
  protected final long blockNumber = 0L;

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
