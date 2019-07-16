package hera.spec.transaction.dsl;

import hera.api.model.Aer;

public interface NeedAmount<NextStateT> {

  /**
   * Accept {@code amount}.
   *
   * @param amount an amount
   * @param unit an amount unit
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
