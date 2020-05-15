/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import hera.AbstractTestCase;
import hera.api.model.ContractAddress;
import hera.client.AergoClient;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import org.junit.Test;

public class ContractApiImplTest extends AbstractTestCase {

  @Test
  public void testStep() {
    final ContractAddress contractAddress = new AergoKeyGenerator().create().getAddress()
        .adapt(ContractAddress.class);
    final ContractApi<ContractTest> contractApi =
        new ContractApiImpl<ContractTest>(contractAddress, new ContractTestImpl(),
            mock(ContractInvocationHandler.class));
    final Signer signer = new AergoKeyGenerator().create();
    ContractTest proxy1 = contractApi.with(mock(AergoClient.class)).execution(signer);
    ContractTest proxy2 = contractApi.with(mock(AergoClient.class)).query();
    assertNotNull(proxy1);
    assertNotNull(proxy2);
  }

  private interface ContractTest {

  }

  private class ContractTestImpl implements ContractTest {

  }

}
