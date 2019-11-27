/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.internal;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Fee;
import hera.contract.ContractApi;
import hera.keystore.InMemoryKeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import org.junit.Test;

public class ContractApiImplTest extends AbstractTestCase {

  private interface ContractTest {

  }

  private class ContractTestImpl implements ContractTest, ContractInvocationPreparable {

    @Override
    public void setWalletApi(WalletApi walletApi) {}

    @Override
    public void setFee(Fee fee) {}

  }

  @Test
  public void testStep() {
    final ContractApi<ContractTest> contractApi =
        new ContractApiImpl<ContractTest>(new ContractTestImpl());
    final WalletApi walletApi = new WalletApiFactory().create(new InMemoryKeyStore());
    final Fee fee = Fee.ZERO;
    final ContractTest withFee = contractApi.walletApi(walletApi).fee(fee);
    final ContractTest withoutFee = contractApi.walletApi(walletApi).noFee();
    assertNotNull(withFee);
    assertNotNull(withoutFee);
  }

}
