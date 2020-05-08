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
public class TxReceipt {

  public static final TxReceipt EMPTY = TxReceipt.newBuilder().build();

  @NonNull
  @Default
  protected final AccountAddress accountAddress = AccountAddress.EMPTY;

  @NonNull
  @Default
  protected final String status = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  protected final String result = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  protected final TxHash txHash = TxHash.of(BytesValue.EMPTY);

  @NonNull
  @Default
  protected final Aer feeUsed = Aer.EMPTY;

  @NonNull
  @Default
  protected final Aer cumulativeFeeUsed = Aer.EMPTY;

  @Default
  protected final long blockNumber = 0L;

  @NonNull
  @Default
  protected final BlockHash blockHash = BlockHash.EMPTY;

  @Default
  protected final int indexInBlock = 0;

  @NonNull
  @Default
  protected final AccountAddress sender = AccountAddress.EMPTY;

  @NonNull
  @Default
  protected final AccountAddress recipient = AccountAddress.EMPTY;

  @Default
  protected final boolean feeDelegation = false;

  @Default
  protected final long gasUsed = 0L;
}
