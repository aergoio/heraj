/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.api.encode.Decoder;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;

public interface Verifier {

  /**
   * Check if {@code Transaction} is valid.
   *
   * @param transaction transaction to verify
   * @return if valid
   */
  boolean verify(Transaction transaction);

  /**
   * Check if {@code base64EncodedSignature} is valid for signer {@code accountAddress} and
   * {@code message}. It hashes {@code message} and verify hashed one.
   *
   * @param accountAddress a signer address
   * @param message a message
   * @param base64EncodedSignature a base64 encoded signature
   *
   * @return if valid
   */
  boolean verifyMessage(AccountAddress accountAddress, String message,
      String base64EncodedSignature);

  /**
   * Check if {@code encodedSignature} is valid for signer {@code accountAddress} and
   * {@code message}. It hashes {@code message} and verify hashed one.
   *
   * @param accountAddress a signer address
   * @param message a message
   * @param encodedSignature an encoded signature
   * @param decoder a decoder to decode encoded signature
   *
   * @return if valid
   */
  boolean verifyMessage(AccountAddress accountAddress, String message,
      String encodedSignature, Decoder decoder);

  /**
   * Check if {@code signature} is valid for signer {@code accountAddress} and {@code message}. It
   * hashes {@code message} and verify hashed one.
   *
   * @param accountAddress a signer address
   * @param message a message
   * @param signature a signature to verify
   *
   * @return if valid
   */
  boolean verifyMessage(AccountAddress accountAddress, BytesValue message, Signature signature);

  /**
   * Check if {@code signature} is valid for {@code accountAddress} and {@code hashedMessage}.
   *
   * @param accountAddress a signer address
   * @param hashedMessage a sha256-hashed message
   * @param signature a signature to verify
   *
   * @return if valid
   */
  boolean verifyMessage(AccountAddress accountAddress, Hash hashedMessage, Signature signature);

}
