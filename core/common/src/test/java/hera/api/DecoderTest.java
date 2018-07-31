/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.util.HexUtils;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;

public class DecoderTest extends AbstractTestCase {

  @Test
  public void testDefaultDecoder() throws IOException {
    Decoder decoder = Decoder.defaultDecoder;
    decoder.decode(new StringReader(HexUtils.encode(randomUUID().toString().getBytes())));
  }

}