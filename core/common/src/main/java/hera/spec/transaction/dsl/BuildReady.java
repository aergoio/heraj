package hera.spec.transaction.dsl;

import hera.api.model.RawTransaction;

public interface BuildReady {

  /**
   * Build raw transaction with provided values.
   *
   * @return a raw transaction
   */
  RawTransaction build();

}
