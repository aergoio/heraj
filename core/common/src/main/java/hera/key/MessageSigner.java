/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encoder;
import hera.api.model.BytesValue;
import hera.api.model.Signature;

@ApiAudience.Public
@ApiStability.Unstable
public interface MessageSigner extends WithPrincipal {

  /**
   * Sha256 hash to {@code message} and sign to it.
   *
   * @param message a message to sign
   * @return base64 encoded signature
   */
  String signMessage(String message);

  /**
   * Sha256 hash to {@code message} and sign to it.
   *
   * @param message a message to sign
   * @param encoder an encoder to encode signed message
   * @return an encoded signature
   */
  String signMessage(String message, Encoder encoder);

  /**
   * Sha256 hash to {@code message} and sign to it.
   *
   * @param message a message to sign
   * @return a signature
   */
  Signature signMessage(BytesValue message);

}
