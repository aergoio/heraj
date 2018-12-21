/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static hera.util.ValidationUtils.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractAddress;
import org.junit.Test;

public class ContractClientFactoryTest extends AbstractTestCase {

  protected final ContractAddress contractAddress = ContractAddress
      .of("AmgSvgaUXFb4PFLn5MU5wiYNihcQrwNhNMzaG5h68q99gBNTTkWK");

  @Test
  public void testCreate() {
    final SmartContract contractSample = new SmartContractFactory()
        .create(SmartContract.class, contractAddress, getClass().getClassLoader());
    assertNotNull(contractSample);
  }

}
