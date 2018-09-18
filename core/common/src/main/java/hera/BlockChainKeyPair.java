/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.util.pki.KeyPair;

public interface BlockChainKeyPair extends KeyPair {

  /**
   * Return encoded private key.
   *
   * @return an encoded private key
   */
  String getEncodedPrivateKey();

  /**
   * Return address corresponding to public key in a encoded form.
   *
   * @return an encoded address
   */
  String getEncodedAddress();

}
