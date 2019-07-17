package hera.spec.transaction.dsl;

import hera.api.model.Identity;

public interface NeedSender<NextStateT> {

  /**
   * Accept transaction sender.
   *
   * @param sender a sender
   * @return next state after accepting sender
   */
  NextStateT from(Identity sender);

  /**
   * Accept transaction sender as an account address or an name.
   *
   * @param sender a sender
   * @return next state after accepting sender
   */
  NextStateT from(String sender);

}
