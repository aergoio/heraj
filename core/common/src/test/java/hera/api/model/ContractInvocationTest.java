/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.exception.HerajException;
import org.junit.Test;

public class ContractInvocationTest {

  public static final String FUNCTION_NAME = randomUUID().toString();

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testBuilder() {
    final ContractAddress address = new ContractAddress(() -> ENCODED_ADDRESS);
    final ContractFunction function = new ContractFunction(FUNCTION_NAME);

    final ContractInvocation expected = new ContractInvocation();
    expected.setAddress(address);
    expected.setFunction(function);
    final ContractInvocation actual =
        ContractInvocation.newBuilder().address(address).function(FUNCTION_NAME).build();
    assertEquals(expected, actual);
  }

  @Test
  public void testBuilderWithArgs() {
    final ContractAddress address = new ContractAddress(() -> ENCODED_ADDRESS);
    final ContractFunction function = new ContractFunction(FUNCTION_NAME);
    final Object[] args = new Object[] {1, 2, 3};

    final ContractInvocation expected = new ContractInvocation();
    expected.setAddress(address);
    expected.setFunction(function);
    expected.setArgs(args);
    final ContractInvocation actual =
        ContractInvocation.newBuilder().address(address).function(FUNCTION_NAME).args(args).build();
    assertEquals(expected, actual);
  }

  @Test
  public void testBuilderUsingInterface() {
    final ContractAddress address = new ContractAddress(() -> ENCODED_ADDRESS);
    final ContractFunction function = new ContractFunction(FUNCTION_NAME);

    final ContractInterface contractInterface = new ContractInterface();
    contractInterface.setContractAddress(address);
    contractInterface.addFunction(function);

    final ContractInvocation expected = new ContractInvocation();
    expected.setAddress(address);
    expected.setFunction(function);
    final ContractInvocation actual = ContractInvocation.newBuilder()
        .contractInterface(contractInterface).function(FUNCTION_NAME).build();
    assertEquals(expected, actual);
  }

  @Test
  public void testBuilderUsingInterfaceWithInvalidFunctionName() {
    final ContractInterface contractInterface = new ContractInterface();
    contractInterface.setContractAddress(new ContractAddress(() -> ENCODED_ADDRESS));
    contractInterface.addFunction(new ContractFunction(FUNCTION_NAME));

    try {
      ContractInvocation.newBuilder().contractInterface(contractInterface)
          .function(randomUUID().toString()).build();
      fail();
    } catch (HerajException e) {
      // good we expected this
    }
  }

  @Test
  public void testBuilderUsingInterfaceWithArgs() {
    final ContractAddress address = new ContractAddress(() -> ENCODED_ADDRESS);
    final ContractFunction function = new ContractFunction(FUNCTION_NAME);
    final Object[] args = new Object[] {1, 2, 3};

    final ContractInterface contractInterface = new ContractInterface();
    contractInterface.setContractAddress(new ContractAddress(() -> ENCODED_ADDRESS));
    contractInterface.addFunction(new ContractFunction(FUNCTION_NAME));

    final ContractInvocation expected = new ContractInvocation();
    expected.setAddress(address);
    expected.setFunction(function);
    expected.setArgs(args);
    final ContractInvocation actual = ContractInvocation.newBuilder()
        .contractInterface(contractInterface).function(FUNCTION_NAME).args(args).build();
    assertEquals(expected, actual);
  }

}
