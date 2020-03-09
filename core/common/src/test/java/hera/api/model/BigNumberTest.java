/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertEquals;

import hera.AbstractTestCase;
import java.util.Map;
import org.junit.Test;

public class BigNumberTest extends AbstractTestCase {

  @Test
  public void testConvertWithMap() {
    final BigNumber expected = BigNumber.of("100");
    final Map<String, String> map = expected.toMap();
    final BigNumber actual = BigNumber.of(map);
    assertEquals(expected, actual);
  }

}
