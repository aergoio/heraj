/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Optional.ofNullable;

import hera.exception.HerajException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ContractInvocation {

  /**
   * Build contract invocation.
   *
   * @param address contract address
   * @param function invocation function
   * @param args arguments of invocation function
   *
   * @return created {@code ContractInvocation}
   */
  public static ContractInvocation of(final ContractAddress address,
      final ContractFunction function, final Object... args) {
    return new ContractInvocation(address, function, args);
  }

  public static ContractInvocationWithNothing newBuilder() {
    return new ContractInvocation.Builder();
  }

  @Setter
  @Getter
  protected ContractAddress address;

  @Setter
  @Getter
  protected ContractFunction function;

  @Setter
  @Getter
  protected Object[] args = new Object[0];

  /**
   * Contract invocation constructor.
   *
   * @param address contract address
   * @param function invocation function
   * @param args arguments of invocation function
   */
  public ContractInvocation(final ContractAddress address, final ContractFunction function,
      final Object... args) {
    this.address = address;
    this.function = function;
    this.args = args;
  }

  public interface ContractInvocationWithNothing {
    ContractInvocationWithInterface contractInterface(ContractInterface contractInterface);

    ContractInvocationWithAddress address(ContractAddress contractAddress);
  }

  public interface ContractInvocationWithInterface {
    ContractInvocationWithFunction function(String functionName);
  }

  public interface ContractInvocationWithAddress {
    ContractInvocationWithFunction function(String functionName);
  }

  public interface ContractInvocationWithFunction extends hera.util.Builder<ContractInvocation> {
    ContractInvocationWithFunctionAndArgs args(Object... args);
  }

  public interface ContractInvocationWithFunctionAndArgs
      extends hera.util.Builder<ContractInvocation> {
  }

  protected static class Builder implements ContractInvocationWithNothing,
      ContractInvocationWithInterface, ContractInvocationWithAddress,
      ContractInvocationWithFunction, ContractInvocationWithFunctionAndArgs {

    protected ContractInterface contractInterface;

    protected ContractInvocation contractInvocation = new ContractInvocation();

    @Override
    public ContractInvocationWithInterface contractInterface(
        final ContractInterface contractInterface) {
      this.contractInterface = ofNullable(contractInterface)
          .orElseThrow(() -> new HerajException("Contract interface shouldn't be null"));
      this.contractInvocation.setAddress(contractInterface.getContractAddress());
      return this;
    }

    @Override
    public ContractInvocationWithAddress address(final ContractAddress contractAddress) {
      this.contractInvocation.setAddress(contractAddress);
      return this;
    }

    @Override
    public ContractInvocationWithFunction function(final String functionName) {
      ContractFunction contractFunction;
      if (null != contractInterface) {
        contractFunction =
            contractInterface.findFunction(functionName).orElseThrow(() -> new HerajException(
                "No funciton named " + functionName + " in contract interface"));
      } else {
        contractFunction = new ContractFunction(functionName);
      }
      this.contractInvocation.setFunction(contractFunction);
      return this;
    }

    @Override
    public ContractInvocationWithFunctionAndArgs args(final Object... args) {
      this.contractInvocation.setArgs(args);
      return this;
    }

    @Override
    public hera.api.model.ContractInvocation build() {
      return this.contractInvocation;
    }

  }

}
