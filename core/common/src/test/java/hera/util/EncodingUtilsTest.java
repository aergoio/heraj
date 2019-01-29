/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.EncodingUtils.decodeBase58;
import static hera.util.EncodingUtils.decodeBase58WithCheck;
import static hera.util.EncodingUtils.decodeHexa;
import static hera.util.EncodingUtils.encodeBase58;
import static hera.util.EncodingUtils.encodeBase58WithCheck;
import static hera.util.EncodingUtils.encodeHexa;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.exception.DecodingFailureException;
import org.junit.Test;

public class EncodingUtilsTest extends AbstractTestCase {

  @Test
  public void testEncodeAndDecodeHexa() {
    final BytesValue expected = new BytesValue(randomUUID().toString().getBytes());
    final String encoded = encodeHexa(expected);
    assertEquals(expected, decodeHexa(encoded));
  }

  @Test
  public void testEncodeAndDecodeBase58() {
    final BytesValue expected = new BytesValue(randomUUID().toString().getBytes());
    final String encoded = encodeBase58(expected);
    assertEquals(expected, decodeBase58(encoded));
  }

  @Test
  public void testEncodeAndDecodeBase58WithCheckSum() {
    final BytesValue expected = new BytesValue(randomUUID().toString().getBytes());
    final String encoded = encodeBase58WithCheck(expected);
    assertEquals(expected, decodeBase58WithCheck(encoded));
  }

  @Test
  public void testDecodeBase58WithCheckOnInvalidEncoding() {
    final BytesValue expected = new BytesValue(randomUUID().toString().getBytes());
    final String encoded = encodeBase58(expected);
    try {
      decodeBase58WithCheck(encoded);
      fail();
    } catch (DecodingFailureException e) {
      // good we expected this
    }
  }

}
