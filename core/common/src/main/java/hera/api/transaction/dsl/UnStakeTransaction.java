package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface UnStakeTransaction extends AergoTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {

  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {

  }

  interface WithChainIdHashAndSender extends NeedAmount<WithChainIdHashAndSenderAndAmount> {

  }

  interface WithChainIdHashAndSenderAndAmount extends NeedNonce<WithReady> {

  }

  interface WithReady extends BuildReady {

  }

}
