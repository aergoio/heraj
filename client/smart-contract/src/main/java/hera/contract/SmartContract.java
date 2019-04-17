/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Fee;
import hera.wallet.Wallet;

@ApiAudience.Public
@ApiStability.Stable
public interface SmartContract {

  /**
   * Bind a wallet to call smart contract.
   *
   * @param wallet a wallet
   */
  void bind(Wallet wallet);

  /**
   * Bind a fee to be used in invoking smart contract.
   *
   * @param fee a fee
   */
  void bind(Fee fee);

}
