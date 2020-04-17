package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.RawTransaction;

@ApiAudience.Public
@ApiStability.Unstable
public interface BuildReady {

  /**
   * Build raw transaction with provided values.
   *
   * @return a raw transaction
   */
  RawTransaction build();

}
