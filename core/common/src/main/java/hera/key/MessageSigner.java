/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.Signature;

@ApiAudience.Public
@ApiStability.Unstable
public interface MessageSigner {

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
   * @return a signature
   */
  Signature signMessage(BytesValue message);

}
