/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.internal;

import hera.api.model.Fee;
import hera.wallet.WalletApi;

interface ContractInvocationPreparable {

  void setWalletApi(WalletApi walletApi);

  void setFee(Fee fee);

}
