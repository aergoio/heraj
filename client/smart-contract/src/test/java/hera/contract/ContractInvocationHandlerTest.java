/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.Fee;
import hera.client.ContractResultImpl;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ContractInvocationHandlerTest extends AbstractTestCase {

  protected final ContractAddress contractAddress = ContractAddress
      .of("AmgSvgaUXFb4PFLn5MU5wiYNihcQrwNhNMzaG5h68q99gBNTTkWK");

  @Test
  public void testCreate() {
    assertNotNull(new ContractInvocationHandler(contractAddress));
    try {
      new ContractInvocationHandler(null);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testGetWallet() {
    final ContractInvocationHandler invocationHandler =
        new ContractInvocationHandler(contractAddress);

    try {
      invocationHandler.getWallet();
      fail();
    } catch (Exception e) {
      // good we expected this
    }

    invocationHandler.setWallet(new WalletBuilder().build(WalletType.Naive));
    assertNotNull(invocationHandler.getWallet());
  }

  @Test
  public void testConfigMethods() throws Throwable {
    final SimpleSmartContract smartContract = new SmartContractFactory()
        .create(SimpleSmartContract.class, contractAddress);
    smartContract.bind(Fee.getDefaultFee());
    smartContract.bind(new WalletBuilder().build(WalletType.Naive));
  }

  @Test
  public void testObjectMethods() {
    final SimpleSmartContract smartContract = new SmartContractFactory()
        .create(SimpleSmartContract.class, contractAddress);
    smartContract.bind(mock(Wallet.class));

    assertNotNull(smartContract.toString());
    assertNotNull(smartContract.hashCode());
    assertFalse(smartContract.equals(null));
  }

  @Test
  public void testUserMethods() {
    final SimpleSmartContract smartContract = new SmartContractFactory()
        .create(SimpleSmartContract.class, contractAddress);
    final Wallet mockWallet = mock(Wallet.class);
    final List<ContractFunction> contractFunctions = new ArrayList<>();
    contractFunctions.add(new ContractFunction("testExecute", asList("arg")));
    contractFunctions.add(new ContractFunction("testQuery"));
    final ContractInterface contractInterface =
        new ContractInterface(contractAddress, "1.0", "lua", contractFunctions);
    when(mockWallet.getContractInterface(contractAddress)).thenReturn(contractInterface);
    when(mockWallet.query(any()))
        .thenReturn(new ContractResultImpl("1".getBytes()));
    smartContract.bind(mockWallet);

    smartContract.testExecute(randomUUID().toString().hashCode());
    assertNotNull(smartContract.testQuery());
  }

  protected interface SimpleSmartContract extends SmartContract {

    void testExecute(int arg);

    int testQuery();

  }

}
