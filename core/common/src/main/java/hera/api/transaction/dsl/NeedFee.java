package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Fee;

@ApiAudience.Public
@ApiStability.Unstable
public interface NeedFee<NextStateT> {

  /**
   * Accept {@code fee} to be used in transaction.
   *
   * @param fee a fee
   * @return next state after accepting fee
   */
  NextStateT fee(Fee fee);

}
