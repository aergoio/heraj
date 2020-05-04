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

  /**
   * Use {@code aergoClient} for contract interaction.
   *
   * @param aergoClient an aergoClient to use
   * @return a prepared contract api
   */
  PreparedContractApi<ContractT> with(AergoClient aergoClient);

}
