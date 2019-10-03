/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import hera.api.model.AccountAddress;

public interface WithPrincipal {

  /**
   * Get principal of a signer.
   *
   * @return a identitprincipal of signer
   */
  AccountAddress getPrincipal();

}
