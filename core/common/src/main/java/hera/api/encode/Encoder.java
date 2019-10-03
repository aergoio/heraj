/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import static hera.util.IoUtils.from;

import hera.util.Base58Utils;
import hera.util.Base64Utils;
import hera.util.HexUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public interface Encoder {

  Encoder Hex = new Encoder() {
    @Override
    public Reader encode(InputStream in) throws IOException {
      return new StringReader(HexUtils.encode(from(in)));
    }
  };

  Encoder Base58 = new Encoder() {
    @Override
    public Reader encode(InputStream in) throws IOException {
      return new StringReader(Base58Utils.encode(from(in)));
    }
  };

  Encoder Base64 = new Encoder() {
    @Override
    public Reader encode(InputStream in) throws IOException {
      return new StringReader(Base64Utils.encode(from(in)));
    }
  };

  Encoder defaultEncoder = Hex;

  Reader encode(InputStream in) throws IOException;

}
