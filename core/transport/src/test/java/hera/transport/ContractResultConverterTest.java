/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractResult;
import org.junit.Test;
import types.Rpc;

public class ContractResultConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractResult, Rpc.SingleBytes> converter =
        new ContractResultConverterFactory().create();

    final ContractResult contractResult =
        converter.convertToDomainModel(Rpc.SingleBytes.newBuilder().build());
    assertNotNull(contractResult);
  }

}
