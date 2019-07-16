/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.internal.Time;
import hera.util.StringUtils;
import java.util.Map;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class ModuleStatus {

  @NonNull
  @Default
  String moduleName = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  String status = StringUtils.EMPTY_STRING;

  long processedMessageCount;

  long queuedMessageCount;

  @NonNull
  @Default
  Time latency = Time.of(0L);

  @NonNull
  @Default
  String error = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  Map<String, Object> actor = emptyMap();

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
