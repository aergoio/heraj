/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import hera.api.model.BytesValue;
import hera.util.Base58Utils;
import java.io.IOException;

public interface Base58 extends EncodedString {

  @Override
  default BytesValue decode() throws IOException {
    return BytesValue.of(Base58Utils.decode(getEncodedValue()));
  }

}
