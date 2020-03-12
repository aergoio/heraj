package hera.api.transaction.dsl;

import hera.api.model.ContractInvocation;

public interface InvokeContractTransaction extends AergoTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {

  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {

  }

  interface WithChainIdHashAndSender {

    /**
     * Accept contract invocation.
     *
     * @param contractInvocation a contract invocation
     * @return next state after accepting contract invocation
     */
    WithChainIdHashAndSenderAndInvocation invocation(ContractInvocation contractInvocation);
  }

  interface WithChainIdHashAndSenderAndInvocation extends NeedNonce<WithReady> {

  }

  interface WithReady extends NeedFee<WithReady>, BuildReady {

  }

}
