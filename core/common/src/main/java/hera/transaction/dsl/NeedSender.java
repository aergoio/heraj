package hera.transaction.dsl;

import hera.api.model.Account;
import hera.api.model.AccountAddress;

public interface NeedSender<NextStateT> {

  /**
   * Accept transaction sender as name.
   *
   * @param senderName a name of sender
   * @return next state after accepting sender
   */
  NextStateT from(String senderName);

  /**
   * Accept transaction sender.
   *
   * @param sender a sender
   * @return next state after accepting sender
   */
  NextStateT from(Account sender);

  /**
   * Accept transaction sender.
   *
   * @param sender a sender
   * @return next state after accepting sender
   */
  NextStateT from(AccountAddress sender);

}
