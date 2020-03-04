/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.Stake;
import org.junit.Test;

public class StakePayloadConverterTest extends AbstractTestCase {

  @Test
  public void testConvertToPayload() {
    final PayloadConverter<Stake> converter = new StakePayloadConverter();
    final Stake stake = new Stake();
    final BytesValue payload = converter.convertToPayload(stake);
    assertNotNull(payload);
  }

}
