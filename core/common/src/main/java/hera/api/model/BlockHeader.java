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
public class BlockHeader {

  public static final BlockHeader EMPTY = BlockHeader.newBuilder().build();

  @NonNull
  @Default
  protected final BytesValue chainId = BytesValue.EMPTY;

  @NonNull
  @Default
  protected final BlockHash previousHash = BlockHash.EMPTY;

  @Default
  protected final long blockNumber = 0L;

  @Default
  protected final long timestamp = 0L;

  @NonNull
  @Default
  protected final Hash rootHash = Hash.EMPTY;

  @NonNull
  @Default
  protected final Hash txRootHash = Hash.EMPTY;

  @NonNull
  @Default
  protected final Hash receiptRootHash = Hash.EMPTY;

  @Default
  protected final long confirmsCount = 0L;

  @NonNull
  @Default
  protected final BytesValue publicKey = BytesValue.EMPTY;

  @NonNull
  @Default
  protected final AccountAddress coinbaseAccount = AccountAddress.EMPTY;

  @NonNull
  @Default
  protected final Signature sign = Signature.EMPTY;

}
