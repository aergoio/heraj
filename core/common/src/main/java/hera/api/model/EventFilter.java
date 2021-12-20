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
@Getter
@ToString
@EqualsAndHashCode
public class EventFilter {

  public static EventFilterBuilder newBuilder() {
    return new EventFilterBuilder();
  }

  public static EventFilterBuilder newBuilder(final ContractAddress contractAddress) {
    return new EventFilterBuilder().contractAddress(contractAddress);
  }

  protected final ContractAddress contractAddress;

  protected final String eventName;

  protected final List<Object> args;

  /**
   * the lower side of block number. inclusive
   */
  protected final long fromBlockNumber;

  /**
   * the higher side of block number. inclusive
   */
  protected final long toBlockNumber;

  protected final boolean decending;

  protected final int recentBlockCount;

  EventFilter(final ContractAddress contractAddress, final String eventName,
      final List<Object> args, long fromBlockNumber, final long toBlockNumber,
      final boolean decending, final int recentBlockCount) {
    assertNotNull(contractAddress, "Contract address must not null");
    assertNotNull(eventName, "Event name must not null");
    assertNotNull(args, "Event args must not null");
    assertTrue(fromBlockNumber >= 0, "From block number must be >= 0");
    assertTrue(toBlockNumber >= 0, "To block number must be >= 0");
    assertTrue(recentBlockCount >= 0, "Recent block count must be >= 0");
    this.contractAddress = contractAddress;
    this.eventName = eventName;
    this.args = unmodifiableList(args);
    this.fromBlockNumber = fromBlockNumber;
    this.toBlockNumber = toBlockNumber;
    this.decending = decending;
    this.recentBlockCount = recentBlockCount;
  }

  public static class EventFilterBuilder implements hera.util.Builder<EventFilter> {

    protected ContractAddress contractAddress;

    protected String eventName = StringUtils.EMPTY_STRING;

    protected List<Object> args = emptyList();

    protected long fromBlockNumber = 0L;

    protected long toBlockNumber = 0L;

    protected boolean decending = false;

    protected int recentBlockCount = 0;

    EventFilterBuilder() {
    }

    public EventFilterBuilder contractAddress(final ContractAddress contractAddress) {
      this.contractAddress = contractAddress;
      return this;
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
