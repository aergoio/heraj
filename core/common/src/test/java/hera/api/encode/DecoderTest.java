/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.util.Base58Utils;
import hera.util.Base64Utils;
import hera.util.HexUtils;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;

public class DecoderTest extends AbstractTestCase {

  @Test
  public void testHexDecoder() throws IOException {
    Decoder decoder = Decoder.Hex;
    decoder.decode(new StringReader(HexUtils.encode(randomUUID().toString().getBytes())));
  }

  @Test
  public void testBase58Decoder() throws IOException {
    Decoder decoder = Decoder.Base58;
    decoder.decode(new StringReader(Base58Utils.encode(randomUUID().toString().getBytes())));
  }

  @Test
  public void testBase58CheckDecoder() throws IOException {
    Decoder decoder = Decoder.Base58Check;
    decoder
        .decode(new StringReader(Base58Utils.encodeWithCheck(randomUUID().toString().getBytes())));
  }

  @Test
  public void testBase64Decoder() throws IOException {
    Decoder decoder = Decoder.Base64;
    decoder.decode(new StringReader(Base64Utils.encode(randomUUID().toString().getBytes())));
  }

}
