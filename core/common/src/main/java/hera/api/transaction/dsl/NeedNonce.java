package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface NeedNonce<NextStateT> {

  /**
   * Accept {@code nonce} to be used in transaction.
   *
   * @param nonce a nonce
   * @return next state after accepting nonce
   */
  NextStateT nonce(long nonce);

}
