/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;

@ApiAudience.Public
@ApiStability.Unstable
public interface Verifier {

  /**
   * Check if {@code Transaction} is valid.
   *
   * @param transaction transaction to verify
   * @return if valid
   */
  boolean verify(Transaction transaction);

  /**
   * Verify {@code signature} for {@code hashedMessage}.
   *
   * @param accountAddress an accountAddress
   * @param hashedMessage a hashed message
   * @param signature a signature
   * @return if valid
   */
  boolean verify(AccountAddress accountAddress, Hash hashedMessage, Signature signature);

  /**
   * Sha256 hash to {@code message} and verify {@code signature} for it.
   *
   * @param accountAddress an accountAddress
   * @param message a message
   * @param signature a signature
   * @return if valid
   */
  boolean verify(AccountAddress accountAddress, BytesValue message, Signature signature);

}
