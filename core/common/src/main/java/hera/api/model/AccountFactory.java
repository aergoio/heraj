/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.model.internal.AccountWithAddress;
import hera.api.model.internal.AccountWithAddressAndSigner;
import hera.api.model.internal.AccountWithKey;
import hera.key.AergoKey;
import hera.key.Signer;

public class AccountFactory {

  public Account create(final AccountAddress accountAddress) {
    return new AccountWithAddress(accountAddress);
  }

  public Account create(final AccountAddress accountAddress, final Signer signer) {
    return new AccountWithAddressAndSigner(accountAddress, signer);
  }

  public Account create(final AergoKey aergoKey) {
    return new AccountWithKey(aergoKey);
  }

}
