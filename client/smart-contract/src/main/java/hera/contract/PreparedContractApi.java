package hera.contract;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.key.Signer;

@ApiAudience.Public
@ApiStability.Unstable
public interface PreparedContractApi<ContractT> {

  ContractT execution(Signer signer);

  ContractT query();

}
