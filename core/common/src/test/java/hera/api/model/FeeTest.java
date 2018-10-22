/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FeeTest {

  @Test
  public void testCreation() {
    final Object[][] testParameters =
        {{-1L, -1L, Fee.MIN_PRICE, Fee.MIN_LIMIT}, {0L, -1L, 0L, Fee.MIN_LIMIT},
            {-1L, 0L, Fee.MIN_PRICE, 0L}, {0L, 0L, 0L, 0L}, {1L, 1L, 1L, 1L}};

    for (final Object[] testParameter : testParameters) {
      final long price = (long) testParameter[0];
      final long limit = (long) testParameter[1];
      final long priceExpected = (long) testParameter[2];
      final long limitExpected = (long) testParameter[3];

      final Fee fee = Fee.of(price, limit);
      assertEquals(priceExpected, fee.getPrice());
      assertEquals(limitExpected, fee.getLimit());
    }
  }

}
