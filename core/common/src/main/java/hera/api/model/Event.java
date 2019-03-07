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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class Event {

  @Getter
  protected final ContractAddress from;

  @Getter
  protected final String name;

  @Getter
  protected final List<Object> args;

  @Getter
  protected final int index;

  @Getter
  protected final TxHash txHash;

  @Getter
  protected final int indexInBlock;

  @Getter
  protected final BlockHash blockHash;

  @Getter
  protected final long blockNumber;

  /**
   * Event constructor.
   *
   * @param from a contract address where event comes from
   * @param name an event name
   * @param args an event arguments
   * @param index an event index
   * @param txHash a transaction hash where contract call is confirmed
   * @param indexInBlock a transaction index in block
   * @param blockHash a block hash where contract call is confirmed
   * @param blockNumber a block block number where contract call is confirmed
   */
  @ApiAudience.Private
  public Event(final ContractAddress from, final String name, final List<Object> args,
      final int index, final TxHash txHash, int indexInBlock, final BlockHash blockHash,
      final long blockNumber) {
    assertNotNull(from, "Contract address must not null");
    assertNotNull(txHash, "Event transaction hash must not null");
    assertNotNull(blockHash, "Event block hash must not null");
    this.from = from;
    this.name = name == null ? StringUtils.EMPTY_STRING : name;
    this.args = unmodifiableList(args == null ? emptyList() : args);
    this.index = index;
    this.txHash = txHash;
    this.indexInBlock = indexInBlock;
    this.blockHash = blockHash;
    this.blockNumber = blockNumber;
  }

}
