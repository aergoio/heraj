package hera.spec.transaction.dsl;

public interface StakeTransaction {

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
