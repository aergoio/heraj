/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

import hera.api.model.BytesValue;

public interface Encoded {

  String getEncodedValue();

  BytesValue decode();

}
