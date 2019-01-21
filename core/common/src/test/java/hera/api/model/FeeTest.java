/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;

import hera.api.model.Aer.Unit;
import org.junit.Test;

public class FeeTest {

  @Test
  public void testCreation() {
    final Object[][] testParameters = {
        {Aer.of("0.1", Unit.AERGO), 100L, Aer.of("0.1", Unit.AERGO), 100L},
        {Aer.of("0.1", Unit.AERGO), -1L, Aer.of("0.1", Unit.AERGO), 0L},
        {Aer.of("0", Unit.AER), 100L, Aer.ZERO, 100L}
    };

    for (final Object[] testParameter : testParameters) {
      final Aer price = (Aer) testParameter[0];
      final long limit = (Long) testParameter[1];
      final Aer priceExpected = (Aer) testParameter[2];
      final long limitExpected = (Long) testParameter[3];

      final Fee fee = Fee.of(price, limit);
      assertEquals(priceExpected, fee.getPrice());
      assertEquals(limitExpected, fee.getLimit());
    }
  }

}
