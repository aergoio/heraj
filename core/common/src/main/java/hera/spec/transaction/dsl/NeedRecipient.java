package hera.spec.transaction.dsl;

import hera.api.model.Account;
import hera.api.model.AccountAddress;

public interface NeedRecipient<NextStateT> {

  /**
   * Accept transaction recipient as name.
   *
   * @param recipientName a name of recipient
   * @return next state after accepting recipient
   */
  NextStateT to(String recipientName);

  /**
   * Accept transaction recipient.
   *
   * @param recipient a recipient
   * @return next state after accepting recipient
   */
  NextStateT to(Account recipient);

  /**
   * Accept transaction recipient.
   *
   * @param recipient a recipient
   * @return next state after accepting recipient
   */
  NextStateT to(AccountAddress recipient);

}
