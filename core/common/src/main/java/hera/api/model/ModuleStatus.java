/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Collections.unmodifiableMap;

import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ModuleStatus {

  @Getter
  protected final String moduleName;

  @Getter
  protected final String status;

  @Getter
  protected final long processedMessageCount;

  @Getter
  protected final long queuedMessageCount;

  @Getter
  protected final Time latency;

  @Getter
  protected final String error;

  @Getter
  protected final Map<String, Object> actor;

  /**
   * ModuleStatus constructor.
   *
   * @param moduleName a module name
   * @param status a module status
   * @param processedMessageCount a processed message count
   * @param queuedMessageCount a queued message count
   * @param latency a latency
   * @param error a kind of error
   * @param actor an actor status
   */
  public ModuleStatus(final String moduleName, final String status,
      final long processedMessageCount,
      final long queuedMessageCount, final Time latency, final String error,
      final Map<String, Object> actor) {
    this.moduleName = moduleName;
    this.status = status;
    this.processedMessageCount = processedMessageCount;
    this.queuedMessageCount = queuedMessageCount;
    this.latency = latency;
    this.error = error;
    this.actor = actor != null ? unmodifiableMap(actor) : null;
  }

}
