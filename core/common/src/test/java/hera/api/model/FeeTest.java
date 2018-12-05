/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.math.BigInteger.valueOf;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import org.junit.Test;

public class FeeTest {

  @Test
  public void testCreation() {
    final Object[][] testParameters =
        {{valueOf(-1L), -1L, Fee.MIN_PRICE, Fee.MIN_LIMIT},
            {valueOf(0L), -1L, valueOf(0L), Fee.MIN_LIMIT},
            {valueOf(-1L), 0L, Fee.MIN_PRICE, 0L}, {valueOf(0L), 0L, valueOf(0L), 0L},
            {valueOf(1L), 1L, valueOf(1L), 1L}};

    for (final Object[] testParameter : testParameters) {
      final BigInteger price = (BigInteger) testParameter[0];
      final long limit = (long) testParameter[1];
      final BigInteger priceExpected = (BigInteger) testParameter[2];
      final long limitExpected = (long) testParameter[3];

      final Fee fee = Fee.of(price, limit);
      assertEquals(priceExpected, fee.getPrice());
      assertEquals(limitExpected, fee.getLimit());
    }
  }

}
