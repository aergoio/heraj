/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;
import org.junit.Test;
import org.slf4j.Logger;

public class FeeTest {

  protected final transient Logger logger = getLogger(getClass());

  @SuppressWarnings("unchecked")
  @Test
  public void testCreation() {
    final Object[][] testParameters = {{empty(), empty(), Fee.MIN_PRICE, Fee.MIN_LIMIT},
        {of(-1L), of(-1L), Fee.MIN_PRICE, Fee.MIN_LIMIT},
        {empty(), of(-1L), Fee.MIN_PRICE, Fee.MIN_LIMIT},
        {of(-1L), empty(), Fee.MIN_PRICE, Fee.MIN_LIMIT}, {of(0L), of(0L), 0L, 0L},
        {empty(), of(0L), Fee.MIN_PRICE, 0L}, {of(0L), empty(), 0L, Fee.MIN_LIMIT},
        {of(1L), of(1L), 1L, 1L}, {empty(), of(1L), Fee.MIN_PRICE, 1L},
        {of(1L), empty(), 1L, Fee.MIN_LIMIT},};

    for (final Object[] testParameter : testParameters) {
      final Optional<Long> price = (Optional<Long>) testParameter[0];
      final Optional<Long> limit = (Optional<Long>) testParameter[1];
      final long priceExpected = (long) testParameter[2];
      final long limitExpected = (long) testParameter[3];

      final Fee fee = Fee.of(price, limit);
      assertEquals(priceExpected, fee.getPrice());
      assertEquals(limitExpected, fee.getLimit());
    }
  }

}
