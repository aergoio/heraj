/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableMap;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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
public class ServerInfo {

  @NonNull
  @Default
  protected final Map<String, String> status = unmodifiableMap(
      Collections.<String, String>emptyMap());

  @NonNull
  @Default
  protected final Map<String, Map<String, String>> config = unmodifiableMap(
      Collections.<String, Map<String, String>>emptyMap());

  ServerInfo(final Map<String, String> status,
      final Map<String, Map<String, String>> config) {
    assertNotNull(status, "Server status list must not null");
    assertNotNull(config, "Module status list must not null");
    this.status = unmodifiableMap(status);
    this.config = unmodifiableMap(config);
  }

}
