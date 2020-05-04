/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractAddress;
import hera.api.model.Time;
import hera.api.model.TryCountAndInterval;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class ContractApiFactoryTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final ContractAddress contractAddress = new AergoKeyGenerator().create().getAddress()
        .adapt(ContractAddress.class);
    final Class<TestContract> type = TestContract.class;
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(3, Time.of(100L));
    final ContractApiFactory contractApiFactory = new ContractApiFactory();
    assertNotNull(contractApiFactory.create(contractAddress, type));
    assertNotNull(contractApiFactory.create(contractAddress, type, tryCountAndInterval));
    assertNotNull(contractApiFactory.create(contractAddress, type, getClass().getClassLoader()));
    assertNotNull(contractApiFactory
        .create(contractAddress, type, tryCountAndInterval, getClass().getClassLoader()));
  }

  private interface TestContract {

  }

}
