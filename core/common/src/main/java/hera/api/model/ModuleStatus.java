/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableMap;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
import java.util.Collections;
import java.util.Map;
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
public class ModuleStatus {

  @NonNull
  @Default
  protected final String moduleName = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  protected final String status = StringUtils.EMPTY_STRING;

  @Default
  protected final long processedMessageCount = 0L;

  @Default
  protected final long queuedMessageCount = 0L;

  @NonNull
  @Default
  protected final Time latency = Time.of(0L);

  @NonNull
  @Default
  protected final String error = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  protected final Map<String, Object> actor = unmodifiableMap(
      Collections.<String, Object>emptyMap());

  ModuleStatus(final String moduleName, final String status, final long processedMessageCount,
      final long queuedMessageCount, final Time latency, final String error,
      final Map<String, Object> actor) {
    assertNotNull(moduleName, "Module name must not null");
    assertNotNull(status, "Module status must not null");
    assertNotNull(latency, "Module latency must not null");
    assertNotNull(error, "Module error must not null");
    assertNotNull(actor, "Actor must not null");
    this.moduleName = moduleName;
    this.status = status;
    this.processedMessageCount = processedMessageCount;
    this.queuedMessageCount = queuedMessageCount;
    this.latency = latency;
    this.error = error;
    this.actor = unmodifiableMap(actor);
  }

}
