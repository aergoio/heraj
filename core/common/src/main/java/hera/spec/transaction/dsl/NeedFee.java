package hera.spec.transaction.dsl;

import hera.api.model.Fee;

public interface NeedFee<NextStateT> {

  /**
   * Accept {@code fee} to be used in transaction.
   *
   * @param fee a fee
   * @return next state after accepting fee
   */
  NextStateT fee(Fee fee);

}
