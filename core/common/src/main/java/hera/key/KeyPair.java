/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.EncryptedPrivateKey;
import java.security.PrivateKey;
import java.security.PublicKey;

@ApiAudience.Public
@ApiStability.Unstable
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
   * Return encrypted private key.
   *
   * @param password encrypt key
   * @return encrypted key
   */
  EncryptedPrivateKey export(String password);

  /**
   * Get account address.
   *
   * @return account address
   */
  AccountAddress getAddress();

}
