/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.util.IoUtils.from;

import hera.util.HexUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public interface Encoder {
  Encoder defaultEncoder = in -> new StringReader(HexUtils.encode(from(in)));

  Reader encode(InputStream in) throws IOException;

}
