/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
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
public class PeerMetric {

  @NonNull
  @Default
  protected final String peerId = StringUtils.EMPTY_STRING;

  @Default
  protected final long sumIn = 0L;

  @Default
  protected final long averageIn = 0L;

  @Default
  protected final long sumOut = 0L;

  @Default
  protected final long averageOut = 0L;

}
