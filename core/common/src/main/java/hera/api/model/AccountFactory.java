/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.api.model.internal.AccountWithAddress;
import hera.api.model.internal.AccountWithAddressAndSigner;
import hera.api.model.internal.AccountWithKey;
import hera.key.AergoKey;
import hera.key.TxSigner;

@Deprecated
public class AccountFactory {

  /**
   * Create an account with address.
   *
   * @param accountAddress an account address
   * @return created account
   */
  @ApiAudience.Public
  public Account create(final AccountAddress accountAddress) {
    assertNotNull(accountAddress);
    return new AccountWithAddress(accountAddress);
  }

  /**
   * Create an account with aergo key.
   *
   * @param aergoKey an aergo key
   * @return created account
   */
  @ApiAudience.Public
  public Account create(final AergoKey aergoKey) {
    assertNotNull(aergoKey);
    return new AccountWithKey(aergoKey);
  }

  /**
   * Create an account with address and signer.
   *
   * @param accountAddress an account address
   * @param txSigner a tx signer
   * @return created account
   */
  @ApiAudience.Private
  public Account create(final AccountAddress accountAddress, final TxSigner txSigner) {
    assertNotNull(accountAddress);
    assertNotNull(txSigner);
    return new AccountWithAddressAndSigner(accountAddress, txSigner);
  }

}
