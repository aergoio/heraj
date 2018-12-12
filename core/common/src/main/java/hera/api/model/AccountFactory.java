/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.model.internal.AccountWithKey;
import hera.api.model.internal.AccountWithoutKey;
import hera.key.AergoKey;

public class AccountFactory {

  public Account create(final AccountAddress accountAddress) {
    return new AccountWithoutKey(accountAddress);
  }

  public Account create(final AergoKey aergoKey) {
    return new AccountWithKey(aergoKey);
  }

}
