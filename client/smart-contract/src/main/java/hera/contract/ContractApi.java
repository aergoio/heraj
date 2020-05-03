/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.client.AergoClient;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractApi<ContractT> {

  PreparedContractApi<ContractT> with(AergoClient aergoClient);

}
