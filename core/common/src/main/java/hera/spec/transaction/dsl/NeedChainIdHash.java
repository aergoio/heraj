package hera.spec.transaction.dsl;

import hera.api.model.ChainIdHash;

public interface NeedChainIdHash<NextStateT> {

  /**
   * Accept {@code chainIdHash} to be used in transaction.
   *
   * @param chainIdHash a chain id hash
   * @return next state after accepting chainIdHash
   */
  NextStateT chainIdHash(ChainIdHash chainIdHash);

}
