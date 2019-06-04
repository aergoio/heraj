package hera.transaction.dsl;

import hera.api.model.Transaction.TxType;

public interface NeedTxType<NextStateT> {

  /**
   * Accept transaction type.
   *
   * @param txType a transaction type
   * @return next state after accepting transaction type
   */
  NextStateT type(TxType txType);

}
