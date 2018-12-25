/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.internal.AccountWithAddress;
import hera.api.model.internal.AccountWithAddressAndSigner;
import hera.api.model.internal.AccountWithKey;
import hera.key.AergoKey;
import hera.key.Signer;

@ApiAudience.Public
@ApiStability.Unstable
public class AccountFactory {

  /**
   * Create an account with address.
   *
   * @param accountAddress an account address
   * @return created account
   */
  @ApiAudience.Public
  public Account create(final AccountAddress accountAddress) {
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
    return new AccountWithKey(aergoKey);
  }

  @ApiAudience.Private
  public Account create(final AccountAddress accountAddress, final Signer signer) {
    return new AccountWithAddressAndSigner(accountAddress, signer);
  }

}
