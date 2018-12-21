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

  protected final String functionName = randomUUID().toString();

  protected final String encodedAddress =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testInvocationBuilder() {
    final ContractAddress address = new ContractAddress(encodedAddress);
    final String version = randomUUID().toString();
    final String language = randomUUID().toString();
    final ContractFunction function = new ContractFunction(functionName);
    final List<ContractFunction> functions = new ArrayList<>();
    functions.add(function);
    final Object[] args = new Object[] {randomUUID().toString(), randomUUID().toString()};

    final ContractInterface contractInterface =
        new ContractInterface(address, version, language, functions);

    final ContractInvocation expected = new ContractInvocation(address, function, args);
    final ContractInvocation actual = contractInterface.newInvocationBuilder()
        .function(functionName)
        .args(args)
        .build();
    assertEquals(expected, actual);
  }

}
