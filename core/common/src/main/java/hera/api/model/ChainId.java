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
public class ChainId {

  public static final ChainId EMPTY = ChainId.newBuilder().build();

  @NonNull
  @Default
  protected final String magic = StringUtils.EMPTY_STRING;

  @Default
  protected final boolean isPublic = false;

  @Default
  protected final boolean isMainNet = false;

  @NonNull
  @Default
  protected final String consensus = StringUtils.EMPTY_STRING;

}
