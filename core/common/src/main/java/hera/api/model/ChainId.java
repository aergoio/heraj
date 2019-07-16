/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class ChainId {

  @NonNull
  @Default
  String magic = StringUtils.EMPTY_STRING;

  boolean isPublic;

  boolean isMainNet;

  @NonNull
  @Default
  String consensus = StringUtils.EMPTY_STRING;

}
