/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ContractInterfaceTest {

  protected final String functionName = randomUUID().toString();

  protected final String encodedAddress =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testInvocationBuilder() {
    final ContractAddress address = new ContractAddress(encodedAddress);
    final String version = randomUUID().toString();
    final String language = randomUUID().toString();
    final ContractFunction function = new ContractFunction(functionName);
    final List<ContractFunction> functions = new ArrayList<ContractFunction>();
    functions.add(function);
    final List<StateVariable> stateVariables = new ArrayList<StateVariable>();

    final ContractInterface contractInterface =
        new ContractInterface(address, version, language, functions, stateVariables);

    final Object[] args = new Object[] {randomUUID().toString(), randomUUID().toString()};
    final ContractInvocation expected =
        new ContractInvocation(address, function, asList(args), Aer.ZERO, true);
    final ContractInvocation actual = contractInterface.newInvocationBuilder()
        .function(functionName)
        .args(args)
        .amount(Aer.ZERO)
        .delegateFee(true)
        .build();
    assertEquals(expected, actual);
  }

  @Test
  public void testInvocationBuilderWithInvalidFunction() {
    final ContractAddress address = new ContractAddress(encodedAddress);
    final String version = randomUUID().toString();
    final String language = randomUUID().toString();
    final ContractFunction function = new ContractFunction(functionName);
    final List<ContractFunction> functions = new ArrayList<ContractFunction>();
    functions.add(function);
    final List<StateVariable> stateVariables = new ArrayList<StateVariable>();


    final ContractInterface contractInterface =
        new ContractInterface(address, version, language, functions, stateVariables);

    final Object[] args = new Object[] {randomUUID().toString(), randomUUID().toString()};
    try {
      contractInterface.newInvocationBuilder()
          .function(randomUUID().toString())
          .args(args)
          .build();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
