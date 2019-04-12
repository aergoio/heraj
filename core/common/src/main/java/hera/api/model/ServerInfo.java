/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableMap;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class ServerInfo {

  @Getter
  protected final Map<String, String> status;

  @Getter
  protected final Map<String, Map<String, String>> config;

  /**
   * ServerInfo constructor.
   *
   * @param status a server status
   * @param config a server config
   */
  @ApiAudience.Private
  public ServerInfo(final Map<String, String> status,
      final Map<String, Map<String, String>> config) {
    assertNotNull(status, "Server status list must not null");
    assertNotNull(config, "Module status list must not null");
    this.status = unmodifiableMap(status);
    this.config = unmodifiableMap(config);
  }



}
