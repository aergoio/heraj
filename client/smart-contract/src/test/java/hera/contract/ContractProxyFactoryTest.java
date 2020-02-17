/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractAddress;
import hera.contract.ContractProxyFactory;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class ContractProxyFactoryTest extends AbstractTestCase {

  private interface ContractTest {

  }

  @Test
  public void testCreate() {
    final ContractAddress contractAddress =
        new AergoKeyGenerator().create().getAddress().adapt(ContractAddress.class);
    final ContractTest proxy =
        new ContractProxyFactory<ContractTest>().create(contractAddress, ContractTest.class,
            getClass().getClassLoader());
    assertNotNull(proxy);
  }

}
