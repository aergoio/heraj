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
public class ContractTxReceipt {

  @NonNull
  @Default
  ContractAddress contractAddress = ContractAddress.EMPTY;

  @NonNull
  @Default
  String status = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  String ret = StringUtils.EMPTY_STRING;

}
