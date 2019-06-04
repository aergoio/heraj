/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.api.model.BytesValue.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.exception.DecodingFailureException;
import hera.exception.InvalidVersionException;
import hera.spec.ContractDefinitionSpec;
import hera.util.EncodingUtils;
import org.junit.Test;

public class ContractDefinitionTest {

  public static final String payload =
      EncodingUtils.encodeBase58WithCheck(of(new byte[] {ContractDefinitionSpec.PAYLOAD_VERSION}));

  @Test
  public void testBuilder() {
    final Object[] args = new Object[] {1, 2, 3};

    final ContractDefinition expected = new ContractDefinition(payload, asList(args), Aer.ONE);
    final ContractDefinition actual = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .constructorArgs(args)
        .amount(Aer.ONE)
        .build();
    assertEquals(expected, actual);
  }

  @Test
  public void testBuildWithInvalidPayload() {
    try {
      final String wrongEncodedPayload = randomUUID().toString();
      new ContractDefinition(wrongEncodedPayload, emptyList(), Aer.ONE);
      fail();
    } catch (DecodingFailureException e) {
      // good we expected this
    }

    try {
      final String wrongVersionPayload =
          EncodingUtils.encodeBase58WithCheck(of(new byte[] {(byte) 0xAA}));
      new ContractDefinition(wrongVersionPayload, emptyList(), Aer.ONE);
      fail();
    } catch (InvalidVersionException e) {
      // good we expected this
    }

  }

}
