/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Fee;
import hera.wallet.WalletApi;

@ApiAudience.Public
@ApiStability.Unstable
public interface ContractApi<ContractT> {

  /**
   * Use a {@code walletApi}.
   *
   * @param walletApi a wallet api
   * 
   * @return an instance of this
   */
  ContractApiWithWalletApi<ContractT> walletApi(WalletApi walletApi);

  interface ContractApiWithWalletApi<ContractT> {

    /**
     * Prepare {@link ContractApi} with a fee.
     *
     * @param fee a fee
     *
     * @return an contract
     */
    ContractT fee(Fee fee);

    /**
     * Prepare {@link ContractApi} with no fee.
     * 
     * @return an contract
     */
    ContractT noFee();
  }

}
