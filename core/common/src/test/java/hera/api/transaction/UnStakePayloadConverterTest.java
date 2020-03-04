/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.UnStake;
import org.junit.Test;

public class UnStakePayloadConverterTest extends AbstractTestCase {

  @Test
  public void testConvertToPayload() {
    final PayloadConverter<UnStake> converter = new UnStakePayloadConverter();
    final UnStake unStake = new UnStake();
    final BytesValue payload = converter.convertToPayload(unStake);
    assertNotNull(payload);
  }

}
