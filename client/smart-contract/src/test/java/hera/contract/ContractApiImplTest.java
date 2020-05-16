/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import hera.AbstractTestCase;
import hera.api.model.ContractAddress;
import hera.api.model.Time;
import hera.api.model.TryCountAndInterval;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.client.NonceRefreshingTxRequester;
import hera.client.TxRequester;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import java.lang.reflect.Proxy;
import org.junit.Test;

public class ContractApiImplTest extends AbstractTestCase {

  @Test
  public void testStep() {
    final ContractAddress contractAddress = new AergoKeyGenerator().create().getAddress()
        .adapt(ContractAddress.class);
    final ContractInvocationHandler invocationHandler = mock(ContractInvocationHandler.class);
    final ContractTest proxy = (ContractTest) Proxy
        .newProxyInstance(getClass().getClassLoader(), new Class[]{ContractTest.class},
            invocationHandler);
    final ContractApi<ContractTest> contractApi = new ContractApiImpl<>(contractAddress, proxy,
        invocationHandler);
    final Signer signer = new AergoKeyGenerator().create();
    ContractTest proxy1 = contractApi.with(mock(AergoClient.class)).execution(signer);
    ContractTest proxy2 = contractApi.with(mock(AergoClient.class)).query();
    assertNotNull(proxy1);
    assertNotNull(proxy2);
  }

  @Test
  public void testToString() throws InterruptedException {
    final ContractAddress contractAddress = new AergoKeyGenerator().create().getAddress()
        .adapt(ContractAddress.class);
    final TxRequester txRequester = new NonceRefreshingTxRequester(TryCountAndInterval.of(3,
        Time.of(1000L)), new SimpleNonceProvider());
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, txRequester);
    final ContractTest contractTest = (ContractTest) Proxy
        .newProxyInstance(getClass().getClassLoader(), new Class[]{ContractTest.class},
            invocationHandler);
    final ContractApi<ContractTest> contractApi = new ContractApiImpl<>(contractAddress,
        contractTest, invocationHandler);
    assertTrue(contractApi.toString().contains(contractAddress.toString()));
  }

  private interface ContractTest {

  }

}
