package hera.spec.transaction.dsl;

import hera.api.model.ContractDefinition;

public interface DeployContractTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {
  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {
  }

  interface WithChainIdHashAndSender extends NeedNonce<WithChainIdHashAndSenderAndNonce> {
  }

  interface WithChainIdHashAndSenderAndNonce {
    /**
     * Accept contract definition.
     *
     * @param contractDefinition a contract definition
     * @return next state after accepting contract definition
     */
    WithReady definition(ContractDefinition contractDefinition);
  }

  interface WithReady extends NeedFee<WithReady>, BuildReady {
  }

}
