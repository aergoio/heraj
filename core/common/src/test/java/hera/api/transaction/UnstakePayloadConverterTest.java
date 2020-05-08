/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.Unstake;
import org.junit.Test;

public class UnstakePayloadConverterTest extends AbstractTestCase {

  @Test
  public void testConvertToPayload() {
    final PayloadConverter<Unstake> converter = new UnstakePayloadConverter();
    final Unstake unStake = Unstake.newBuilder().build();
    final BytesValue payload = converter.convertToPayload(unStake);
    assertNotNull(payload);
  }

}
