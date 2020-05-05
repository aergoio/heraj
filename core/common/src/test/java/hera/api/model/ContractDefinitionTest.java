/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.exception.HerajException;
import hera.util.Base58Utils;
import java.util.List;
import org.junit.Test;

public class ContractDefinitionTest {

  protected final String payload =
      Base58Utils.encodeWithCheck(new byte[]{ContractDefinition.PAYLOAD_VERSION});

  @Test
  public void testBuilder() {
    final Object[] args = new Object[]{1, 2, 3};
    final ContractDefinition expected = new ContractDefinition(payload, asList(args), Aer.ONE);
    final ContractDefinition actual = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .constructorArgs(args)
        .amount(Aer.ONE)
        .build();
    assertEquals(expected, actual);
  }

  @Test
  public void testBuilderWithListArgs() {
    final List<Object> args = asList(new Object[]{1, 2, 3});
    final ContractDefinition expected = new ContractDefinition(payload, args, Aer.ONE);
    final ContractDefinition actual = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .constructorArgs(args)
        .amount(Aer.ONE)
        .build();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldThrowExceptionOnPayloadWithoutVersion() {
    try {
      final String withoutVersion = Base58Utils.encodeWithCheck(new byte[]{(byte) 0xAA});
      ContractDefinition.newBuilder()
          .encodedContract(withoutVersion)
          .amount(Aer.ONE)
          .build();
      fail();
    } catch (HerajException e) {
      // then
    }
  }

}
