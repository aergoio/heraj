/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import hera.util.StringUtils;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class ContractInterface {

  @Getter
  @Default
  protected final ContractAddress address = ContractAddress.EMPTY;

  @Getter
  @Default
  protected final String version = StringUtils.EMPTY_STRING;

  @Getter
  @Default
  protected final String language = StringUtils.EMPTY_STRING;

  @Getter
  @Default
  protected final List<ContractFunction> functions = emptyList();

  @Getter
  @Default
  protected final List<StateVariable> stateVariables = emptyList();

  /**
   * Find a contract function with the given function name.
   *
   * @param functionName function name to find
   * @return {@code ContractFunction} if found. Otherwise, null
   */
  public ContractFunction findFunction(final String functionName) {
    ContractFunction foundFunction = null;
    for (final ContractFunction contractFunction : functions) {
      if (contractFunction.getName().equals(functionName)) {
        foundFunction = contractFunction;
        break;
      }
    }
    return foundFunction;
  }

  public ContractInvocationWithNothing newInvocationBuilder() {
    return new ContractInterface.InvocationBuilder(this);
  }

  public interface ContractInvocationWithNothing {

    ContractInvocationWithReady function(String functionName);
  }

  public interface ContractInvocationWithReady extends hera.util.Builder<ContractInvocation> {

    ContractInvocationWithReady args(List<Object> args);

    ContractInvocationWithReady args(Object... args);

    ContractInvocationWithReady amount(Aer amount);

    ContractInvocationWithReady delegateFee(boolean delegateFee);
  }

  @RequiredArgsConstructor
  protected static class InvocationBuilder
      implements ContractInvocationWithNothing, ContractInvocationWithReady {

    @NonNull
    protected final ContractInterface contractInterface;

    protected ContractFunction function;

    protected List<Object> args = emptyList();

    protected Aer amount = Aer.EMPTY;

    protected boolean delegateFee = false;

    @Override
    public ContractInvocationWithReady function(final String functionName) {
      this.function = contractInterface.findFunction(functionName);
      if (null == this.function) {
        throw new HerajException(
            "Cannot find function from interface [name: " + functionName + "]");
      }
      return this;
    }

    @Override
    public ContractInvocationWithReady args(final Object... args) {
      if (null != args) {
        this.args = asList(args);
      }
      return this;
    }

    @Override
    public ContractInvocationWithReady args(final List<Object> args) {
      if (null != args) {
        this.args = args;
      }
      return this;
    }

    @Override
    public ContractInvocationWithReady amount(final Aer amount) {
      this.amount = amount;
      return this;
    }

    @Override
    public ContractInvocationWithReady delegateFee(final boolean delegateFee) {
      if (delegateFee && !this.function.isFeeDelegation()) {
        throw new HerajException("Target function cannot delegate fee");
      }
      this.delegateFee = delegateFee;
      return this;
    }

    @Override
    public hera.api.model.ContractInvocation build() {
      return ContractInvocation.newBuilder()
          .address(contractInterface.getAddress())
          .functionName(function.getName())
          .args(args)
          .amount(amount)
          .delegateFee(delegateFee)
          .build();
    }

  }

}
