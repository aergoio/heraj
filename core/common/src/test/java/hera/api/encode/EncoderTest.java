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
  public void testDefaultEncoder() throws IOException {
    final Encoder encoder = Encoder.defaultEncoder;
    encoder.encode(new ByteArrayInputStream(randomUUID().toString().getBytes()));
  }

}