/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ContractInterfaceTest {

  public static final String FUNCTION_NAME = randomUUID().toString();

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testInvocationBuilder() {
    final ContractAddress address = new ContractAddress(() -> ENCODED_ADDRESS);
    final String version = randomUUID().toString();
    final String language = randomUUID().toString();
    final ContractFunction function = new ContractFunction(FUNCTION_NAME);
    final List<ContractFunction> functions = new ArrayList<>();
    functions.add(function);
    final Object[] args = new Object[] {randomUUID().toString(), randomUUID().toString()};

    final ContractInterface contractInterface =
        new ContractInterface(address, version, language, functions);

    final ContractInvocation expected = new ContractInvocation(address, function, args);
    final ContractInvocation actual = contractInterface.newInvocationBuilder()
        .function(FUNCTION_NAME)
        .args(args)
        .build();
    assertEquals(expected, actual);
  }

}
