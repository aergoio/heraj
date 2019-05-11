/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
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

  @Getter
  protected final Aer amount;

  /**
   * Contract invocation constructor.
   *
   * @param contractAddress contract address
   * @param contractFunction invocation function
   */
  @ApiAudience.Private
  public ContractInvocation(final ContractAddress contractAddress,
      final ContractFunction contractFunction) {
    this(contractAddress, contractFunction, emptyList());
  }

  /**
   * Contract invocation constructor.
   *
   * @param contractAddress contract address
   * @param contractFunction invocation function
   * @param args arguments of invocation function
   */
  @ApiAudience.Private
  public ContractInvocation(final ContractAddress contractAddress,
      final ContractFunction contractFunction, final List<Object> args) {
    this(contractAddress, contractFunction, args, Aer.ZERO);
  }

  /**
   * Contract invocation constructor.
   *
   * @param contractAddress contract address
   * @param contractFunction invocation function
   * @param amount an amount
   */
  @ApiAudience.Private
  public ContractInvocation(final ContractAddress contractAddress,
      final ContractFunction contractFunction, final Aer amount) {
    this(contractAddress, contractFunction, emptyList(), amount);
  }

  /**
   * Contract invocation constructor.
   *
   * @param contractAddress contract address
   * @param contractFunction invocation function
   * @param args arguments of invocation function
   * @param amount an amount
   */
  @ApiAudience.Private
  public ContractInvocation(final ContractAddress contractAddress,
      final ContractFunction contractFunction, final List<Object> args, final Aer amount) {
    assertNotNull(contractAddress, "Contract address must not null");
    assertNotNull(contractFunction, "Contract function must not null");
    assertNotNull(args, "Contract function args must not null");
    assertNotNull(amount, "Amount must not null");
    this.address = contractAddress;
    this.function = contractFunction;
    this.args = unmodifiableList(args);
    this.amount = amount;
  }

}
