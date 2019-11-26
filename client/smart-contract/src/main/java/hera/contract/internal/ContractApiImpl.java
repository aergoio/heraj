/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.internal;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.Fee;
import hera.contract.ContractApi;
import hera.contract.ContractApi.ContractApiWithWalletApi;
import hera.wallet.WalletApi;

public class ContractApiImpl<ContractT>
    implements ContractApi<ContractT>, ContractApiWithWalletApi<ContractT> {

  protected ContractT contract;

  public ContractApiImpl(final ContractT contract) {
    assertNotNull(contract);
    this.contract = contract;
  }

  @Override
  public ContractApiWithWalletApi<ContractT> walletApi(final WalletApi walletApi) {
    assertNotNull(walletApi);
    ((ContractInvocationPreparable) contract).setWalletApi(walletApi);
    return this;
  }

  @Override
  public ContractT fee(final Fee fee) {
    assertNotNull(fee);
    ((ContractInvocationPreparable) contract).setFee(fee);
    return this.contract;
  }

  @Override
  public ContractT noFee() {
    return this.contract;
  }

}
