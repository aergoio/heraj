/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;
import static java.util.Arrays.asList;
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
public class EventFilter {

  public static EventFilterBuilder newBuilder(final ContractAddress contractAddress) {
    return new EventFilterBuilder(contractAddress);
  }

  @Getter
  protected final ContractAddress contractAddress;

  @Getter
  protected final String eventName;

  @Getter
  protected final List<Object> args;

  @Getter
  protected final long fromBlockNumber;

  @Getter
  protected final long toBlockNumber;

  @Getter
  protected final boolean decending;

  @Getter
  protected final int recentBlockCount;

  /**
   * EventFilter constructor.
   *
   * @param contractAddress a contract address where event comes from
   * @param eventName an event name
   * @param args an event arguments
   * @param fromBlockNumber a block number where search starts
   * @param toBlockNumber a block number where search finishes
   * @param decending whether to search block in a decending order or not
   * @param recentBlockCount a recent block count
   */
  @ApiAudience.Private
  public EventFilter(final ContractAddress contractAddress, final String eventName,
      final List<Object> args, long fromBlockNumber, final long toBlockNumber,
      final boolean decending, final int recentBlockCount) {
    assertNotNull(contractAddress, "Contract address must not null");
    assertTrue(fromBlockNumber >= 0, "From block number must be >= 0");
    assertTrue(toBlockNumber >= 0, "To block number must be >= 0");
    assertTrue(recentBlockCount >= 0, "Recent block count must be >= 0");
    this.contractAddress = contractAddress;
    this.eventName = null == eventName ? StringUtils.EMPTY_STRING : eventName;
    this.args = unmodifiableList(args == null ? emptyList() : args);
    this.fromBlockNumber = fromBlockNumber;
    this.toBlockNumber = toBlockNumber;
    this.decending = decending;
    this.recentBlockCount = recentBlockCount;
  }

  public static class EventFilterBuilder implements hera.util.Builder<EventFilter> {

    protected final ContractAddress contractAddress;

    protected String eventName;

    protected List<Object> args;

    protected long fromBlockNumber;

    protected long toBlockNumber;

    protected boolean decending;

    protected int recentBlockCount;

    public EventFilterBuilder(final ContractAddress contractAddress) {
      this.contractAddress = contractAddress;
    }

    public EventFilterBuilder eventName(final String eventName) {
      this.eventName = eventName;
      return this;
    }

    public EventFilterBuilder args(final Object... args) {
      return args(asList(args));
    }

    public EventFilterBuilder args(final List<Object> args) {
      this.args = args;
      return this;
    }

    public EventFilterBuilder fromBlockNumber(final long fromBlockNumber) {
      this.fromBlockNumber = fromBlockNumber;
      return this;
    }

    public EventFilterBuilder toBlockNumber(final long toBlockNumber) {
      this.toBlockNumber = toBlockNumber;
      return this;
    }

    public EventFilterBuilder decending(final boolean decending) {
      this.decending = decending;
      return this;
    }

    public EventFilterBuilder recentBlockCount(final int recentBlockCount) {
      this.recentBlockCount = recentBlockCount;
      return this;
    }

    @Override
    public EventFilter build() {
      return new EventFilter(contractAddress, eventName, args, fromBlockNumber, toBlockNumber,
          decending, recentBlockCount);
    }

  }

}
