/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Test;

public class EncoderTest extends AbstractTestCase {

  @Test
  public void testHexaEncoder() throws IOException {
    final Encoder encoder = Encoder.Hex;
    encoder.encode(new ByteArrayInputStream(randomUUID().toString().getBytes()));
  }

  @Test
  public void testBase58Encoder() throws IOException {
    final Encoder encoder = Encoder.Base58;
    encoder.encode(new ByteArrayInputStream(randomUUID().toString().getBytes()));
  }

  @Test
  public void testBase58CheckEncoder() throws IOException {
    final Encoder encoder = Encoder.Base58Check;
    encoder.encode(new ByteArrayInputStream(randomUUID().toString().getBytes()));
  }

  @Test
  public void testBase64Encoder() throws IOException {
    final Encoder encoder = Encoder.Base64;
    encoder.encode(new ByteArrayInputStream(randomUUID().toString().getBytes()));
  }

}
