package hera.api.transaction.dsl;

import hera.api.model.BytesValue;

public interface NeedPayload<NextStateT> {

  /**
   * Accept {@code payload}.
   *
   * @param payload a payload
   * @return next state after accepting payload
   */
  NextStateT payload(BytesValue payload);

}
