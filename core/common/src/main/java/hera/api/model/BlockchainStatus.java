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
public class BlockchainStatus {

  long bestHeight;

  @NonNull
  @Default
  BlockHash bestBlockHash = BlockHash.of(BytesValue.EMPTY);

  @NonNull
  @Default
  String consensus = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  ChainIdHash chainIdHash = ChainIdHash.of(BytesValue.EMPTY);

}
