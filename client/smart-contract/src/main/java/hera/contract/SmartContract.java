/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.wallet.Wallet;

@ApiAudience.Public
@ApiStability.Unstable
public interface SmartContract {

  void bind(Wallet wallet);

}
