/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class BlockMetadata {

  @NonNull
  @Default
  BlockHash blockHash = BlockHash.of(BytesValue.EMPTY);

  @NonNull
  @Default
  BlockHeader blockHeader = BlockHeader.newBuilder().build();

  int txCount;

  long blockSize; // in bytes

}
