package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Identity;

@ApiAudience.Public
@ApiStability.Unstable
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
