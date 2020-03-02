/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import static hera.util.IoUtils.from;

import hera.util.Base58Utils;
import hera.util.Base64Utils;
import hera.util.HexUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public interface Decoder {

  Decoder Hex = new Decoder() {
    @Override
    public InputStream decode(final Reader reader) throws IOException {
      return new ByteArrayInputStream(HexUtils.decode(from(reader)));
    }
  };

  Decoder Base58 = new Decoder() {
    @Override
    public InputStream decode(final Reader reader) throws IOException {
      return new ByteArrayInputStream(Base58Utils.decode(from(reader)));
    }
  };

  Decoder Base58Check = new Decoder() {
    @Override
    public InputStream decode(final Reader reader) throws IOException {
      return new ByteArrayInputStream(Base58Utils.decodeWithCheck(from(reader)));
    }
  };

  Decoder Base64 = new Decoder() {
    @Override
    public InputStream decode(final Reader reader) throws IOException {
      return new ByteArrayInputStream(Base64Utils.decode(from(reader)));
    }
  };

  InputStream decode(Reader reader) throws IOException;
}
