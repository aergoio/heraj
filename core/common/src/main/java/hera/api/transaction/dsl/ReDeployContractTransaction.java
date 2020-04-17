package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;

@ApiAudience.Public
@ApiStability.Unstable
public interface ReDeployContractTransaction extends AergoTransaction {

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

  interface WithChainIdHashAndCreator {

    /**
     * Accept contract address. The contract address must be existing one.
     *
     * @param contractAddress an existing contract address.
     * @return next state after accepting contract address
     */
    WithChainIdHashAndCreatorAndContractAddress contractAddress(
        ContractAddress contractAddress);
  }

  interface WithChainIdHashAndCreatorAndContractAddress {

    /**
     * Accept contract definition.
     *
     * @param contractDefinition a contract definition to re-deploy
     * @return next state after accepting contract definition
     */
    WithChainIdHashAndCreatorAndContractAddressAndContractDefinition definition(
        ContractDefinition contractDefinition);
  }

  interface WithChainIdHashAndCreatorAndContractAddressAndContractDefinition extends
      NeedNonce<WithReady> {

  }

  interface WithReady extends NeedFee<WithReady>, BuildReady {

  }

}
