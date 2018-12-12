/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import hera.api.model.Fee;
import hera.wallet.Wallet;

public interface SmartContract {

  void bind(Wallet wallet);

  void bind(Fee fee);

}
