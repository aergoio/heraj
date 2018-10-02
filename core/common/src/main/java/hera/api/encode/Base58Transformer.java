/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import static hera.util.IoUtils.from;

import hera.util.Base58Utils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class Base58Transformer implements Transformer {

  @Override
  public Reader encode(InputStream in) throws IOException {
    return new StringReader(Base58Utils.encode(from(in)));
  }

  @Override
  public InputStream decode(Reader reader) throws IOException {
    return new ByteArrayInputStream(Base58Utils.decode(from(reader)));
  }
}
