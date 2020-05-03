/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractAddress;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class ContractApiFactoryTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final ContractAddress anyContractAddress = new AergoKeyGenerator().create().getAddress()
        .adapt(ContractAddress.class);
    final ContractApi<TestContract> contractApi = new ContractApiFactory()
        .create(anyContractAddress, TestContract.class);
    assertNotNull(contractApi);
  }

  private interface TestContract {

  }

}
