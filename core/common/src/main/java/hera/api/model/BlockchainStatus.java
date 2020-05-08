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
public class BlockchainStatus {

  public static final BlockchainStatus EMPTY = BlockchainStatus.newBuilder().build();

  @Default
  protected final long bestHeight = 0L;

  @NonNull
  @Default
  protected final BlockHash bestBlockHash = BlockHash.EMPTY;

  @NonNull
  @Default
  protected final String consensus = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  protected final ChainIdHash chainIdHash = ChainIdHash.EMPTY;

}
