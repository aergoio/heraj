package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BytesValue;

@ApiAudience.Public
@ApiStability.Unstable
public interface NeedPayload<NextStateT> {

  /**
   * Accept {@code payload}.
   *
   * @param payload a payload
   * @return next state after accepting payload
   */
  NextStateT payload(BytesValue payload);

}
