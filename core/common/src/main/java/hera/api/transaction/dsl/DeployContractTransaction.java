package hera.api.transaction.dsl;

import hera.api.model.ContractDefinition;

public interface DeployContractTransaction extends AergoTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {

  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {

  }

  interface WithChainIdHashAndSender {

    /**
     * Accept contract definition.
     *
     * @param contractDefinition a contract definition
     * @return next state after accepting contract definition
     */
    WithChainIdHashAndSenderAndDefinition definition(ContractDefinition contractDefinition);
  }

  interface WithChainIdHashAndSenderAndDefinition extends NeedNonce<WithReady> {

  }

  interface WithReady extends NeedFee<WithReady>, BuildReady {

  }

}
