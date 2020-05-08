/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

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
@Getter
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class ContractInvocation implements Payload {

  @NonNull
  protected final ContractAddress address;

  @ToString.Exclude
  @NonNull
  @Default
  protected final ContractFunction function = ContractFunction.EMPTY; // not used

  @NonNull
  protected final String functionName;

  @NonNull
  @Default
  protected final List<Object> args = unmodifiableList(emptyList());

  @NonNull
  @Default
  protected final Aer amount = Aer.EMPTY;

  @Default
  protected final boolean delegateFee = false;

}
