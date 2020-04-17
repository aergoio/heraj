package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Aer;

@ApiAudience.Public
@ApiStability.Unstable
public interface NeedAmount<NextStateT> {

  /**
   * Accept {@code amount}.
   *
   * @param amount an amount
   * @param unit   an amount unit
   * @return next state after accepting amount
   */
  NextStateT amount(String amount, Aer.Unit unit);

  /**
   * Accept {@code amount} in {@link Aer}.
   *
   * @param amount an amount in aer
   * @return next state after accepting amount
   */
  NextStateT amount(Aer amount);

}
