/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import static hera.util.IoUtils.from;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.HexUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

@ApiAudience.Private
@ApiStability.Unstable
public interface Encoder {
  Encoder defaultEncoder = in -> new StringReader(HexUtils.encode(from(in)));

  Reader encode(InputStream in) throws IOException;

}
