/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractAddress;
import org.junit.Test;

public class ContractApiFactoryTest extends AbstractTestCase {

  protected final ContractAddress contractAddress = ContractAddress
      .of("AmgSvgaUXFb4PFLn5MU5wiYNihcQrwNhNMzaG5h68q99gBNTTkWK");

  @Test
  public void testCreate() {
    final ContractApi<TestContract> contractApi = new ContractApiFactory()
        .create(contractAddress, TestContract.class, getClass().getClassLoader());
    assertNotNull(contractApi);
  }

  public static interface TestContract {

    void execute();

    String query();
  }

}
