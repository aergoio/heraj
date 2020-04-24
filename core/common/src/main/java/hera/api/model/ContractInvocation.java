/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Collections.emptyList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class ContractInvocation implements Payload {

  @Getter
  @NonNull
  protected final ContractAddress address;

  // not used
  @Getter
  @NonNull
  @Default
  protected final ContractFunction function = ContractFunction.EMPTY;

  @Getter
  @NonNull
  protected final String functionName;

  @Getter
  @NonNull
  @Default
  protected final List<Object> args = emptyList();

  @Getter
  @NonNull
  @Default
  protected final Aer amount = Aer.EMPTY;

  @Getter
  @Default
  protected final boolean delegateFee = false;

}
