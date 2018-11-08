/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContractDefinitionTest {

  public static final String payload = randomUUID().toString();

  @Test
  public void testBuilder() {
    final Object[] args = new Object[] {1, 2, 3};

    final ContractDefinition expected = new ContractDefinition();
    expected.setEncodedContract(() -> payload);
    expected.setConstructorArgs(args);
    final ContractDefinition actual = ContractDefinition.newBuilder()
        .contractInPayload(() -> payload).constructorArgs(args).build();
    assertEquals(expected, actual);
  }

}
