/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.Map;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class ServerInfo {

  @NonNull
  @Default
  Map<String, String> status = emptyMap();

  @NonNull
  @Default
  Map<String, Map<String, String>> config = emptyMap();

  ServerInfo(final Map<String, String> status,
      final Map<String, Map<String, String>> config) {
    assertNotNull(status, "Server status list must not null");
    assertNotNull(config, "Module status list must not null");
    this.status = unmodifiableMap(status);
    this.config = unmodifiableMap(config);
  }

}
