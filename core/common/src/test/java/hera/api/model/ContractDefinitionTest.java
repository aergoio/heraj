/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.exception.DecodingFailureException;
import hera.exception.InvalidVersionException;
import hera.util.EncodingUtils;
import org.junit.Test;

public class ContractDefinitionTest {

  public static final String payload =
      EncodingUtils.encodeBase58WithCheck(of(new byte[] {ContractDefinition.PAYLOAD_VERSION}));

  @Test
  public void testBuilder() {
    final Object[] args = new Object[] {1, 2, 3};

    final ContractDefinition expected = new ContractDefinition(payload, args);
    final ContractDefinition actual = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .constructorArgs(args)
        .build();
    assertEquals(expected, actual);
  }

  @Test
  public void testBuildWithInvalidPayload() {
    try {
      final String wrongEncodedPayload = randomUUID().toString();
      new ContractDefinition(wrongEncodedPayload);
      fail();
    } catch (DecodingFailureException e) {
      // good we expected this
    }

    try {
      final String wrongVersionPayload =
          EncodingUtils.encodeBase58WithCheck(of(new byte[] {(byte) 0xAA}));
      new ContractDefinition(wrongVersionPayload);
      fail();
    } catch (InvalidVersionException e) {
      // good we expected this
    }

  }

}
