/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class ContractInvocation {

  @Getter
  protected final ContractAddress address;

  @Getter
  protected final ContractFunction function;

  @Getter
  protected final List<Object> args;

  /**
   * Contract invocation constructor.
   *
   * @param contractAddress contract address
   * @param contractFunction invocation function
   * @param args arguments of invocation function
   */
  @ApiAudience.Private
  public ContractInvocation(final ContractAddress contractAddress,
      final ContractFunction contractFunction,
      final Object... args) {
    assertNotNull(contractAddress, "Contract address must not null");
    assertNotNull(contractFunction, "Contract function must not null");
    this.address = contractAddress;
    this.function = contractFunction;
    this.args = null != args ? unmodifiableList(asList(args)) : unmodifiableList(emptyList());
  }

}
