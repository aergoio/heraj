package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ChainIdHash;

@ApiAudience.Public
@ApiStability.Unstable
public interface NeedChainIdHash<NextStateT> {

  /**
   * Accept {@code chainIdHash} to be used in transaction.
   *
   * @param chainIdHash a chain id hash
   * @return next state after accepting chainIdHash
   */
  NextStateT chainIdHash(ChainIdHash chainIdHash);

}
