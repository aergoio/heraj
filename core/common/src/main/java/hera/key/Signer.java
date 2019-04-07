/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;

@ApiAudience.Public
@ApiStability.Unstable
public interface Signer {

  /**
   * Sign to raw transaction.
   *
   * @param rawTransaction raw transaction to sign
   * @return a signed transaction
   */
  Transaction sign(RawTransaction rawTransaction);

  /**
   * Sign plain text.
   *
   * @param plainText a plain text
   * @return a signature
   */
  Signature sign(BytesValue plainText);

  /**
   * Sign a plain message.
   *
   * @param message a message to sign
   * @return base64 encoded signature
   */
  String signMessage(String message);

}
