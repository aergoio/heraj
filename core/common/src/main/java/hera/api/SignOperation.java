/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.BytesValue;
import hera.util.pki.KeyPair;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public interface SignOperation {

  BytesValue sign(KeyPair key, Supplier<InputStream> plainText) throws IOException;

  boolean verify(KeyPair key, Supplier<InputStream> plainText, BytesValue signature)
      throws IOException;
}
