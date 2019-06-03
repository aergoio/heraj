/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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

}
