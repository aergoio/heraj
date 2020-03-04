/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;

@ApiAudience.Public
@ApiStability.Unstable
public interface Signer extends WithPrincipal, TxSigner {

  /**
   * Sign to raw transaction.
   *
   * @param rawTransaction raw transaction to sign
   * @return a signed transaction
   */
  Transaction sign(RawTransaction rawTransaction);

  /**
   * Sha256 hash to {@code message} and sign to it.
   *
   * @param message a message to sign
   * @return a signature
   */
  Signature signMessage(BytesValue message);

  /**
   * Sign to {@code hashedMessage}.
   *
   * @param hashedMessage a hashed message to sign
   * @return a signature
   */
  Signature signMessage(Hash hashedMessage);

}
