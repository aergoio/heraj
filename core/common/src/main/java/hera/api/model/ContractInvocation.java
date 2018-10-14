/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ContractInvocation {

  @NonNull
  @Getter
  protected ContractAddress address;

  @NonNull
  @Getter
  protected ContractFunction function;

  @Getter
  protected List<Object> args;

  public ContractInvocation(final ContractAddress address, final ContractFunction function) {
    this(address, function, Collections.emptyList());
  }

}
