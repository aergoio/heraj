/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    final ContractFunction function = new ContractFunction(functionName,
        Collections.<String>emptyList(), false, false, true);
    final List<ContractFunction> functions = asList(function);
    final List<StateVariable> stateVariables = new ArrayList<StateVariable>();
    final ContractInterface contractInterface = ContractInterface.newBuilder()
        .address(address)
        .version(version)
        .language(language)
        .functions(functions)
        .stateVariables(stateVariables)
        .build();

    final Object[] args = new Object[]{randomUUID().toString(), randomUUID().toString()};
    final ContractInvocation expected = ContractInvocation
        .newBuilder()
        .address(address)
        .functionName(functionName)
        .args(asList(args))
        .amount(Aer.EMPTY)
        .delegateFee(true)
        .build();
    final ContractInvocation actual = contractInterface.newInvocationBuilder()
        .function(functionName)
        .args(args)
        .amount(Aer.EMPTY)
        .delegateFee(true)
        .build();
    assertEquals(expected, actual);
  }

  @Test
  public void testInvocationBuilderOnListArgs() {
    final ContractAddress address = new ContractAddress(encodedAddress);
    final String version = randomUUID().toString();
    final String language = randomUUID().toString();
    final ContractFunction function = new ContractFunction(functionName,
        Collections.<String>emptyList(), false, false, true);
    final List<ContractFunction> functions = asList(function);
    final List<StateVariable> stateVariables = new ArrayList<StateVariable>();
    final ContractInterface contractInterface = ContractInterface.newBuilder()
        .address(address)
        .version(version)
        .language(language)
        .functions(functions)
        .stateVariables(stateVariables)
        .build();

    final List<Object> args = Arrays.<Object>asList(randomUUID().toString(),
        randomUUID().toString());
    final ContractInvocation expected = ContractInvocation
        .newBuilder()
        .address(address)
        .functionName(functionName)
        .args(args)
        .amount(Aer.EMPTY)
        .delegateFee(true)
        .build();
    final ContractInvocation actual = contractInterface.newInvocationBuilder()
        .function(functionName)
        .args(args)
        .amount(Aer.EMPTY)
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
    final ContractInterface contractInterface = ContractInterface.newBuilder()
        .address(address)
        .version(version)
        .language(language)
        .functions(functions)
        .stateVariables(stateVariables)
        .build();

    final Object[] args = new Object[]{randomUUID().toString(), randomUUID().toString()};
    try {
      contractInterface.newInvocationBuilder()
          .function(randomUUID().toString())
          .args(args)
          .build();
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void testFeeDelegation() {
    final ContractAddress address = new ContractAddress(encodedAddress);
    final String version = randomUUID().toString();
    final String language = randomUUID().toString();
    final ContractFunction function = new ContractFunction(functionName, false, false, true);
    final List<ContractFunction> functions = new ArrayList<ContractFunction>();
    functions.add(function);
    final List<StateVariable> stateVariables = new ArrayList<StateVariable>();
    final ContractInterface contractInterface = ContractInterface.newBuilder()
        .address(address)
        .version(version)
        .language(language)
        .functions(functions)
        .stateVariables(stateVariables)
        .build();

    final ContractInvocation invocation = contractInterface.newInvocationBuilder()
        .function(functionName)
        .amount(Aer.ZERO)
        .delegateFee(true)
        .build();
    assertNotNull(invocation);
  }

  @Test
  public void shouldThrowErrorOnNonFeeDelegateableFunction() {
    // given
    final ContractAddress address = new ContractAddress(encodedAddress);
    final String version = randomUUID().toString();
    final String language = randomUUID().toString();
    final ContractFunction function = new ContractFunction(functionName, false, false, false);
    final List<ContractFunction> functions = new ArrayList<ContractFunction>();
    functions.add(function);
    final List<StateVariable> stateVariables = new ArrayList<StateVariable>();
    final ContractInterface contractInterface = ContractInterface.newBuilder()
        .address(address)
        .version(version)
        .language(language)
        .functions(functions)
        .stateVariables(stateVariables)
        .build();

    try {
      contractInterface.newInvocationBuilder()
          .function(functionName)
          .amount(Aer.ZERO)
          .delegateFee(true)
          .build();
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldNotThrowErrorNotFeeDelegegatableOne() {
    final ContractAddress address = new ContractAddress(encodedAddress);
    final String version = randomUUID().toString();
    final String language = randomUUID().toString();
    final ContractFunction function = new ContractFunction(functionName, false, false, false);
    final List<ContractFunction> functions = new ArrayList<ContractFunction>();
    functions.add(function);
    final List<StateVariable> stateVariables = new ArrayList<StateVariable>();
    final ContractInterface contractInterface = ContractInterface.newBuilder()
        .address(address)
        .version(version)
        .language(language)
        .functions(functions)
        .stateVariables(stateVariables)
        .build();

    final ContractInvocation invocation = contractInterface.newInvocationBuilder()
        .function(functionName)
        .amount(Aer.ZERO)
        .delegateFee(false)
        .build();
    assertNotNull(invocation);
  }

}
