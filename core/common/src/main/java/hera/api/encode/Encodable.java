/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.encode;

public interface Encodable {

  /**
   * Get encoded value.
   *
   * @param encoder an encoder
   * @return an encoded value
   */
  String getEncoded(Encoder encoder);

}
