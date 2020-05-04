/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.ContractOperation;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.Fee;
import hera.api.model.StateVariable;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.client.TxRequestFunction;
import hera.client.TxRequester;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class ContractInvocationHandlerTest extends AbstractTestCase {

  private final ContractAddress contractAddress = new AergoKeyGenerator().create().getAddress()
      .adapt(ContractAddress.class);

  private final ClassLoader classLoader = getClass().getClassLoader();

  @Test
  public void testPrepareClient() {
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mock(
        TxRequester.class));
    final AergoClient aergoClient = new AergoClientBuilder().build();
    invocationHandler.prepareClient(aergoClient);
  }

  @Test
  public void testPrepareSigner() {
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mock(
        TxRequester.class));
    final Signer signer = new AergoKeyGenerator().create();
    invocationHandler.prepareSigner(signer);
  }

  @Test
  public void shouldThrowErrorOnNoClient() throws Exception {
    // given
    final ContractOperation mockContractOperation = mock(ContractOperation.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(contractInterface);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockContractOperation);
    final TxRequester mockTxRequester = mock(TxRequester.class);
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mockTxRequester);

    try {
      // when
      final ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
          new Class<?>[]{ContractTest.class}, invocationHandler);
      final Signer signer = new AergoKeyGenerator().create();
      invocationHandler.prepareSigner(signer);
      proxy.executeVoid();
      fail();
    } catch (Exception e) {
      // then
      assertTrue(e.getMessage().contains("is null"));
    }
  }

  @Test
  public void shouldThrowErrorOnNoSigner() throws Exception {
    // given
    final ContractOperation mockContractOperation = mock(ContractOperation.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(contractInterface);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockContractOperation);
    final TxRequester mockTxRequester = mock(TxRequester.class);
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mockTxRequester);

    try {
      // when
      final ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
          new Class<?>[]{ContractTest.class}, invocationHandler);
      invocationHandler.prepareClient(mockClient);
      proxy.executeVoid();
      fail();
    } catch (Exception e) {
      // then
      assertTrue(e.getMessage().contains("is null"));
    }
  }

  @Test
  public void testExecuteWithoutReturn() throws Exception {
    // given
    final ContractOperation mockContractOperation = mock(ContractOperation.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(contractInterface);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockContractOperation);
    final TxRequester mockTxRequester = mock(TxRequester.class);
    final TxHash expected = TxHash.of(BytesValue.of(randomUUID().toString().getBytes()));
    when(mockTxRequester
        .request(any(AergoClient.class), any(Signer.class), any(TxRequestFunction.class)))
        .thenReturn(expected);
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mockTxRequester);

    // then
    final ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);
    final Signer signer = new AergoKeyGenerator().create();
    invocationHandler.prepareClient(mockClient);
    invocationHandler.prepareSigner(signer);
    proxy.executeVoid();
  }

  @Test
  public void testExecuteWithTxHashReturn() throws Exception {
    // given
    final ContractOperation mockContractOperation = mock(ContractOperation.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(contractInterface);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockContractOperation);
    final TxRequester mockTxRequester = mock(TxRequester.class);
    final TxHash expected = TxHash.of(BytesValue.of(randomUUID().toString().getBytes()));
    when(mockTxRequester
        .request(any(AergoClient.class), any(Signer.class), any(TxRequestFunction.class)))
        .thenReturn(expected);
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mockTxRequester);

    // then
    final ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);
    final Signer signer = new AergoKeyGenerator().create();
    invocationHandler.prepareClient(mockClient);
    invocationHandler.prepareSigner(signer);
    final TxHash actual = proxy.executeTxHash(randomUUID().toString(), randomUUID().toString());
    assertEquals(expected, actual);
  }

  @Test
  public void testExecuteWithFee() throws Exception {
    // given
    final ContractOperation mockContractOperation = mock(ContractOperation.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(contractInterface);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockContractOperation);
    final TxRequester mockTxRequester = mock(TxRequester.class);
    final TxHash expected = TxHash.of(BytesValue.of(randomUUID().toString().getBytes()));
    when(mockTxRequester
        .request(any(AergoClient.class), any(Signer.class), any(TxRequestFunction.class)))
        .thenReturn(expected);
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mockTxRequester);

    // then
    final ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);
    final Signer signer = new AergoKeyGenerator().create();
    invocationHandler.prepareClient(mockClient);
    invocationHandler.prepareSigner(signer);
    final TxHash actual = proxy.executeWithFee(Fee.of(300000L));
    assertEquals(expected, actual);
  }

  @Test
  public void testQuery() throws IOException {
    // given
    final int expected = randomUUID().toString().hashCode();
    final ContractOperation mockContractOperation = mock(ContractOperation.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(contractInterface);
    final ContractResult contractResult = mock(ContractResult.class);
    when(contractResult.bind(any(Class.class))).thenReturn(expected);
    when(mockContractOperation.query(any(ContractInvocation.class))).thenReturn(contractResult);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockContractOperation);
    final TxRequester mockTxRequester = mock(TxRequester.class);
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mockTxRequester);

    // then
    final ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);
    final Signer signer = new AergoKeyGenerator().create();
    invocationHandler.prepareClient(mockClient);
    invocationHandler.prepareSigner(signer);
    final int actual = proxy.query();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldThrowErrorIfReturnOnExecution() throws IOException {
    // given
    final int expected = randomUUID().toString().hashCode();
    final ContractOperation mockContractOperation = mock(ContractOperation.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(contractInterface);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockContractOperation);
    final TxRequester mockTxRequester = mock(TxRequester.class);
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mockTxRequester);

    // when
    final ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);
    final Signer signer = new AergoKeyGenerator().create();
    invocationHandler.prepareClient(mockClient);
    invocationHandler.prepareSigner(signer);

    try {
      proxy.invalidExecute();
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldThrowErrorIfVoidReturnOnQuery() throws IOException {
    // given
    final int expected = randomUUID().toString().hashCode();
    final ContractOperation mockContractOperation = mock(ContractOperation.class);
    final ContractInterface contractInterface = supplyContractInterface();
    when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(contractInterface);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockContractOperation);
    final TxRequester mockTxRequester = mock(TxRequester.class);
    final ContractInvocationHandler invocationHandler = new ContractInvocationHandler(
        contractAddress, mockTxRequester);

    // when
    final ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);
    final Signer signer = new AergoKeyGenerator().create();
    invocationHandler.prepareClient(mockClient);
    invocationHandler.prepareSigner(signer);

    try {
      proxy.invalidQuery();
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
  protected ContractInterface supplyContractInterface() {
    final List<ContractFunction> contractFunctions = asList(
        new ContractFunction("executeVoid", Collections.<String>emptyList(), false, false, false),
        new ContractFunction("executeTxHash", Collections.<String>emptyList(), false, false, false),
        new ContractFunction("executeWithFee", asList("arg"), false, false, false),
        new ContractFunction("query", Collections.<String>emptyList(), false, true, false),
        new ContractFunction("invalidExecute", Collections.<String>emptyList(), false, false,
            false),
        new ContractFunction("invalidQuery", Collections.<String>emptyList(), false, true, false)
    );
    return ContractInterface.newBuilder()
        .address(contractAddress)
        .version("1.0")
        .language("lua")
        .functions(contractFunctions)
        .stateVariables(Collections.<StateVariable>emptyList())
        .build();
  }

  private interface ContractTest {

    void executeVoid();

    TxHash executeTxHash(String a, String b);

    TxHash executeWithFee(Fee fee);

    int query();

    Object invalidExecute();

    void invalidQuery();
  }

}
