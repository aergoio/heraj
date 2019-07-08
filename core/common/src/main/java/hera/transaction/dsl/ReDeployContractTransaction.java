package hera.transaction.dsl;

import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;

public interface ReDeployContractTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {
  }

  interface WithChainIdHash {
    /**
     * Accept contract creator as name.
     *
     * @param creatorName a name of creator
     * @return next state after accepting creator
     */
    WithChainIdHashAndCreator creator(String creatorName);

    /**
     * Accept contract transaction creator.
     *
     * @param creator a creator
     * @return next state after accepting creator
     */
    WithChainIdHashAndCreator creator(AccountAddress creator);
  }

  interface WithChainIdHashAndCreator extends NeedNonce<WithChainIdHashAndCreatorAndNonce> {
  }

  interface WithChainIdHashAndCreatorAndNonce {
    /**
     * Accept contract address. The contract address must be existing one.
     *
     * @param contractAddress an existing contract address.
     * @return next state after accepting contract address
     */
    WithChainIdHashAndCreatorAndNonceAndContractAddress contractAddress(
        ContractAddress contractAddress);
  }

  interface WithChainIdHashAndCreatorAndNonceAndContractAddress {
    /**
     * Accept contract definition.
     *
     * @param contractDefinition a contract definition to re-deploy
     * @return next state after accepting contract definition
     */
    WithReady definition(ContractDefinition contractDefinition);
  }

  interface WithReady extends NeedFee<WithReady>, BuildReady {
  }

}
