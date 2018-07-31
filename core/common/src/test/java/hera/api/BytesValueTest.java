/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

import hera.api.model.BytesValue;
import java.util.Arrays;
import org.junit.Test;

public class BytesValueTest {
  @Test
  public void testHashCode() {
    final byte[] byteValue1 = randomUUID().toString().getBytes();
    final byte[] byteValue2 = Arrays.copyOf(byteValue1, byteValue1.length);

    final BytesValue value1 = new BytesValue(byteValue1);
    final BytesValue value2 = new BytesValue(byteValue2);

    assertEquals(value1.hashCode(), value2.hashCode());
  }

  @Test
  public void testEquals() {
    final byte[] byteValue1 = randomUUID().toString().getBytes();
    final byte[] byteValue2 = Arrays.copyOf(byteValue1, byteValue1.length);

    final BytesValue value1 = new BytesValue(byteValue1);
    final BytesValue value2 = new BytesValue(byteValue2);

    assertEquals(value1, value2);
  }

}