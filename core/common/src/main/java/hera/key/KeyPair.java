/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyPair {

  /**
   * Get private key.
   *
   * @return private key
   */
  PrivateKey getPrivateKey();

  /**
   * Get public key.
   *
   * @return public key
   */
  PublicKey getPublicKey();

  /**
   * Sign to plain text.
   *
   * @param plainText text to sign to
   *
   * @return signature
   */
  BytesValue sign(final InputStream plainText);

  /**
   * Check if {@code signature} is valid for {@code plainText}.
   *
   * @param plainText plain text
   * @param signature raw signature
   *
   * @return if valid
   */
  boolean verify(final InputStream plainText, final BytesValue signature);

  /**
   * Return encoded private key.
   *
   * @return an encoded private key
   */
  String getEncodedPrivateKey();

  /**
   * Get account address.
   *
   * @return account address
   */
  AccountAddress getAddress();

  /**
   * Return address corresponding to public key in a encoded form.
   *
   * @return an encoded address
   */
  String getEncodedAddress();

}
