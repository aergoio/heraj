/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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
public class BlockMetadata {

  public static final BlockMetadata EMPTY = BlockMetadata.newBuilder().build();

  @NonNull
  @Default
  protected final BlockHash blockHash = BlockHash.EMPTY;

  @NonNull
  @Default
  protected final BlockHeader blockHeader = BlockHeader.EMPTY;

  @Default
  protected final int txCount = 0;

  @Default
  protected final long blockSize = 0L; // in bytes

}
