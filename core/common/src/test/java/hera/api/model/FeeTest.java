/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FeeTest {

  @Test
  public void testCreation() {
    final Object[][] testParameters = {
        {"0.1 aergo", -1L, Aer.of("0.1 aergo"), Fee.MIN_LIMIT},
        {"0.1 aergo", 100L, Aer.of("0.1 aergo"), 100L},
        {"0 aer", 100L, Fee.MIN_PRICE, 100L},
        {"0.1 aergo", 100L, Aer.of("0.1 aergo"), 100L}
    };

    for (final Object[] testParameter : testParameters) {
      final String price = (String) testParameter[0];
      final long limit = (long) testParameter[1];
      final Aer priceExpected = (Aer) testParameter[2];
      final long limitExpected = (long) testParameter[3];

      final Fee fee = Fee.of(price, limit);
      assertEquals(priceExpected, fee.getPrice());
      assertEquals(limitExpected, fee.getLimit());
    }
  }

}
