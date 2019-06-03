/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static hera.api.model.BytesValue.of;
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
import hera.api.model.ContractInvocation;
import hera.api.model.StateVariable;
import hera.client.internal.ContractResultImpl;
import hera.exception.ContractException;
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
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockWallet.getContractInterface(contractAddress)).thenReturn(contractInterface);
    when(mockWallet.query(any(ContractInvocation.class)))
        .thenReturn(new ContractResultImpl(of("1".getBytes())));
    smartContract.bind(mockWallet);

    smartContract.testExecute(randomUUID().toString().hashCode());
    assertNotNull(smartContract.testQuery());
  }

  @Test
  public void testExecuteWithQueryFunction() {
    final ExecuteAsQuery smartContract = new SmartContractFactory()
        .create(ExecuteAsQuery.class, contractAddress);

    final Wallet mockWallet = mock(Wallet.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockWallet.getContractInterface(contractAddress)).thenReturn(contractInterface);
    when(mockWallet.query(any(ContractInvocation.class)))
        .thenReturn(new ContractResultImpl(of("1".getBytes())));
    smartContract.bind(mockWallet);

    try {
      smartContract.testQuery();
      fail();
    } catch (ContractException e) {
      // good we expected this
    }
  }

  @Test
  public void testQueryWithExecuteFunction() {
    final QueryAsExecute smartContract = new SmartContractFactory()
        .create(QueryAsExecute.class, contractAddress);

    final Wallet mockWallet = mock(Wallet.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockWallet.getContractInterface(contractAddress)).thenReturn(contractInterface);
    when(mockWallet.query(any(ContractInvocation.class)))
        .thenReturn(new ContractResultImpl(of("1".getBytes())));
    smartContract.bind(mockWallet);

    try {
      smartContract.testExecute(randomUUID().toString().hashCode());
      fail();
    } catch (ContractException e) {
      // good we expected this
    }
  }

  protected ContractInterface supplyContractInterface() {
    final List<ContractFunction> contractFunctions = new ArrayList<ContractFunction>();
    contractFunctions.add(new ContractFunction("testExecute", asList("arg")));
    contractFunctions.add(new ContractFunction("testQuery", false, true));
    final List<StateVariable> stateVariables = new ArrayList<StateVariable>();
    final ContractInterface contractInterface =
        new ContractInterface(contractAddress, "1.0", "lua", contractFunctions, stateVariables);
    return contractInterface;
  }

  protected interface SimpleSmartContract extends SmartContract {

    void testExecute(int arg);

    int testQuery();

  }

  protected interface ExecuteAsQuery extends SmartContract {

    void testQuery();

  }

  protected interface QueryAsExecute extends SmartContract {

    int testExecute(int arg);

  }

}
