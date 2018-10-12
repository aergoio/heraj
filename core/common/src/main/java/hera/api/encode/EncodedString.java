/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import hera.api.model.BytesValue;
import java.io.IOException;

public interface EncodedString {

  String getEncodedValue();

  BytesValue decode() throws IOException;

}
