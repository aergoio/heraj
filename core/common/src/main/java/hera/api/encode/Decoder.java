/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import static hera.util.IoUtils.from;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.HexUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

@ApiAudience.Private
@ApiStability.Unstable
public interface Decoder {
  Decoder defaultDecoder = new Decoder() {
    @Override
    public InputStream decode(Reader reader) throws IOException {
      return new ByteArrayInputStream(HexUtils.decode(from(reader)));
    }
  };

  InputStream decode(Reader reader) throws IOException;
}
