package hera.contract;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.key.Signer;

@ApiAudience.Public
@ApiStability.Unstable
public interface PreparedContractApi<ContractT> {

  /**
   * Get an interface for contract execution.
   *
   * @param signer a signer
   * @return an interface for contract execution
   */
  ContractT execution(Signer signer);

  /**
   * Get an interface for contract query.
   *
   * @return an interface for contract query
   */
  ContractT query();

}
