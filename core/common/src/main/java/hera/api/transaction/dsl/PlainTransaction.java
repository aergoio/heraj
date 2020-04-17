package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public interface PlainTransaction extends AergoTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {

  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {

  }

  interface WithChainIdHashAndSender extends NeedRecipient<WithChainIdHashAndSenderAndRecipient> {

  }

  interface WithChainIdHashAndSenderAndRecipient
      extends NeedAmount<WithChainIdHashAndSenderAndRecipientAndAmount> {

  }

  interface WithChainIdHashAndSenderAndRecipientAndAmount extends NeedNonce<WithReady> {

  }

  interface WithReady extends
      NeedFee<WithReady>,
      NeedPayload<WithReady>,
      NeedTxType<WithReady>,
      BuildReady {

  }

}
