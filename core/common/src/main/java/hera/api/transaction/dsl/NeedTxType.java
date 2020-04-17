package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Transaction.TxType;

@ApiAudience.Public
@ApiStability.Unstable
public interface NeedTxType<NextStateT> {

  /**
   * Accept transaction type.
   *
   * @param txType a transaction type
   * @return next state after accepting transaction type
   */
  NextStateT type(TxType txType);

}
