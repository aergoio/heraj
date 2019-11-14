/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Collections.emptyList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class ContractTxReceipt {

  @NonNull
  @Default
  ContractAddress contractAddress = ContractAddress.EMPTY;

  @NonNull
  @Default
  String status = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  ContractResult ret = ContractResult.EMPTY;

  @NonNull
  @Default
  TxHash txHash = TxHash.of(BytesValue.EMPTY);

  @NonNull
  @Default
  Aer feeUsed = Aer.EMPTY;

  @NonNull
  @Default
  Aer cumulativeFeeUsed = Aer.EMPTY;

  @NonNull
  @Default
  BytesValue bloom = BytesValue.EMPTY;

  @NonNull
  @Default
  List<Event> events = emptyList();

  @Default
  long blockNumber = 0L;

  @Default
  BlockHash blockHash = BlockHash.of(BytesValue.EMPTY);

  @Default
  int indexInBlock = 0;

  @NonNull
  @Default
  AccountAddress sender = AccountAddress.EMPTY;

  @NonNull
  @Default
  AccountAddress recipient = AccountAddress.EMPTY;

  @Default
  boolean feeDelegation = false;

}
