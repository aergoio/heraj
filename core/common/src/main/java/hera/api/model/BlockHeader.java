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
public class BlockHeader {

  @NonNull
  @Default
  BytesValue chainId = BytesValue.EMPTY;

  @NonNull
  @Default
  BlockHash previousHash = BlockHash.of(BytesValue.EMPTY);

  long blockNumber;

  long timestamp;

  @NonNull
  @Default
  Hash rootHash = Hash.of(BytesValue.EMPTY);

  @NonNull
  @Default
  Hash txRootHash = Hash.of(BytesValue.EMPTY);

  @NonNull
  @Default
  Hash receiptRootHash = Hash.of(BytesValue.EMPTY);

  long confirmsCount;

  @NonNull
  @Default
  BytesValue publicKey = BytesValue.EMPTY;

  @NonNull
  @Default
  AccountAddress coinbaseAccount = AccountAddress.EMPTY;

  @NonNull
  @Default
  Signature sign = Signature.EMPTY;

}
