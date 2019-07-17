package hera.spec.transaction.dsl;

import hera.api.model.Identity;

public interface NeedRecipient<NextStateT> {

  /**
   * Accept transaction recipient.
   *
   * @param recipient a recipient
   * @return next state after accepting recipient
   */
  NextStateT to(Identity recipient);

  /**
   * Accept transaction recipient as an account address or an name.
   *
   * @param recipient a recipient
   * @return next state after accepting recipient
   */
  NextStateT to(String recipient);

}
