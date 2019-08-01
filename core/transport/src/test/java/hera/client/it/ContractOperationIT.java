/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.BigNumber;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;

public class ContractOperationIT extends AbstractIT {

  protected String executeFunction = "set";
  protected String executeFunction2 = "set2";
  protected String queryFunction = "get";
  protected String newQueryFunction = "newGet";

  protected String simplePayload;
  protected String withPayablePayload;
  protected String withAbiAddedPayload;
  protected String withEventPayload;
  protected String withBignumPayload;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    simplePayload = IoUtils.from(new InputStreamReader(open("simple_payload")));
    withPayablePayload = IoUtils.from(new InputStreamReader(open("with_payable_payload")));
    withAbiAddedPayload = IoUtils.from(new InputStreamReader(open("with_abi_added_payload")));
    withEventPayload = IoUtils.from(new InputStreamReader(open("with_event_payload")));
    withBignumPayload = IoUtils.from(new InputStreamReader(open("with_bignum_payload")));
  }

  @Test
  public void shouldDeployOnPlainContract() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();

    // when
    final ContractTxReceipt receipt = deploy(key, definition);

    // then
    assertEquals("CREATED", receipt.getStatus());
  }

  @Test
  public void shouldApplyConstructorArgsOnDeploy() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final String deployKey = randomUUID().toString();
    final int deployIntVal = randomUUID().toString().hashCode();
    final String deployStringVal = randomUUID().toString();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .constructorArgs(deployKey, deployIntVal, deployStringVal)
        .build();

    // when
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // then
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function(queryFunction)
        .args(deployKey)
        .build();
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    final Data data = queryResult.bind(Data.class);
    assertEquals(deployIntVal, data.getIntVal());
    assertEquals(deployStringVal, data.getStringVal());
  }

  @Test
  public void shouldDeployFailOnInvaidNonce() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();

    try {
      // when
      aergoClient.getContractOperation().deploy(key, definition, 0L, Fee.ZERO);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldReDeployOnDeployedContract() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();
    final ContractTxReceipt deployReceipt = deploy(key, definition);
    final ContractAddress originAddress = deployReceipt.getContractAddress();

    // when
    final String reDeployKey = randomUUID().toString();
    final int reDeployIntVal = randomUUID().toString().hashCode();
    final String reDeployStringVal = randomUUID().toString();
    final ContractDefinition newDefinition = ContractDefinition.newBuilder()
        .encodedContract(withAbiAddedPayload)
        .constructorArgs(reDeployKey, reDeployIntVal, reDeployStringVal)
        .build();
    final ContractTxReceipt reDeployTxReceipt = redeploy(key, originAddress, newDefinition);

    // then
    assertEquals("RECREATED", reDeployTxReceipt.getStatus());
    assertEquals(originAddress, reDeployTxReceipt.getContractAddress());
    final ContractInterface contractInterface = getAbi(reDeployTxReceipt);
    final ContractFunction newQueryFunc = contractInterface.findFunction(newQueryFunction);
    assertNotNull(newQueryFunc);
  }

  @Test
  public void shouldApplyConstructorArgsOnReDeploy() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();
    final ContractTxReceipt deployReceipt = deploy(key, definition);
    final ContractAddress originAddress = deployReceipt.getContractAddress();

    // when
    final String reDeployKey = randomUUID().toString();
    final int reDeployIntVal = randomUUID().toString().hashCode();
    final String reDeployStringVal = randomUUID().toString();
    final ContractDefinition newDefinition = ContractDefinition.newBuilder()
        .encodedContract(withAbiAddedPayload)
        .constructorArgs(reDeployKey, reDeployIntVal, reDeployStringVal)
        .build();
    final ContractInterface contractInterface =
        redeployAndGetAbi(key, originAddress, newDefinition);

    // then
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function(newQueryFunction)
        .args(reDeployKey)
        .build();
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    final Data data = queryResult.bind(Data.class);
    assertEquals(data.getIntVal(), reDeployIntVal);
    assertEquals(data.getStringVal(), reDeployStringVal);
  }

  @Test
  public void shouldKeepStateVariablesOnReDeploy() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final String deployKey = randomUUID().toString();
    final int deployIntVal = randomUUID().toString().hashCode();
    final String deployStringVal = randomUUID().toString();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .constructorArgs(deployKey, deployIntVal, deployStringVal)
        .build();
    final ContractTxReceipt deployReceipt = deploy(key, definition);
    final ContractAddress originAddress = deployReceipt.getContractAddress();

    // when
    final ContractDefinition newDefinition = ContractDefinition.newBuilder()
        .encodedContract(withAbiAddedPayload)
        .build();
    final ContractInterface contractInterface =
        redeployAndGetAbi(key, originAddress, newDefinition);

    // then
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function(newQueryFunction)
        .args(deployKey)
        .build();
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    final Data data = queryResult.bind(Data.class);
    assertEquals(data.getIntVal(), deployIntVal);
    assertEquals(data.getStringVal(), deployStringVal);
  }

  @Test
  public void shouldNewFunctionSetOnReDeploy() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();
    final ContractTxReceipt deployReceipt = deploy(key, definition);
    final ContractAddress originAddress = deployReceipt.getContractAddress();

    // when
    final ContractDefinition newDefinition = ContractDefinition.newBuilder()
        .encodedContract(withAbiAddedPayload)
        .build();
    final ContractInterface contractInterface =
        redeployAndGetAbi(key, originAddress, newDefinition);

    // then
    final ContractFunction newQueryFunc = contractInterface.findFunction(newQueryFunction);
    assertNotNull(newQueryFunc);
  }

  @Test
  public void shouldHasAmountSameAsDeployedOne() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final Aer expected = Aer.GIGA_ONE;
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withPayablePayload)
        .amount(expected)
        .build();

    // when
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);
    final ContractAddress contractAddress = contractInterface.getAddress();

    // then
    final AccountState state = aergoClient.getAccountOperation().getState(contractAddress);
    final Aer actual = state.getBalance();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldExecuteOnDeployedOne() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final int executeIntVal = randomUUID().toString().hashCode();
    final String executeStringVal = randomUUID().toString();
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(executeKey, executeIntVal, executeStringVal)
        .build();
    final ContractTxReceipt executionReceipt = execute(key, execution);

    // then
    assertEquals("SUCCESS", executionReceipt.getStatus());
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function(queryFunction)
        .args(executeKey)
        .build();
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    final Data data = queryResult.bind(Data.class);
    assertEquals(executeIntVal, data.getIntVal());
    assertEquals(executeStringVal, data.getStringVal());
  }

  @Test
  public void shouldBindExecuteResult() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final int executeIntVal = randomUUID().toString().hashCode();
    final String executeStringVal = randomUUID().toString();
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(executeKey, executeIntVal, executeStringVal)
        .build();
    final ContractTxReceipt executionReceipt = execute(key, execution);

    // then
    final Data data = executionReceipt.getRet().bind(Data.class);
    assertEquals(executeIntVal, data.getIntVal());
    assertEquals(executeStringVal, data.getStringVal());
  }

  @Test
  public void shouldExecuteWithBignumberArguments() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withBignumPayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final BigNumber x = new BigNumber("111");
    final BigNumber y = new BigNumber("222");
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(executeKey, x, y)
        .build();
    execute(key, execution);

    // then
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function(queryFunction)
        .args(executeKey)
        .build();
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    final BigNumber actual = queryResult.bind(BigNumber.class);
    assertEquals(x.add(y), actual);
  }

  @Test
  public void shouldHasAmountSameAsUsedInExecution() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withPayablePayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final int executeIntVal = randomUUID().toString().hashCode();
    final String executeStringVal = randomUUID().toString();
    final Aer expected = Aer.GIGA_ONE;
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(executeKey, executeIntVal, executeStringVal)
        .amount(expected)
        .build();
    execute(key, execution);

    // then
    final AccountState state =
        aergoClient.getAccountOperation().getState(contractInterface.getAddress());
    assertEquals(expected, state.getBalance());
  }

  @Test
  public void shouldExecuteWithEscapeStringAsArgs() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final int executeIntVal = randomUUID().toString().hashCode();
    final String escapeString = "\b\t\f\n\r";
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(executeKey, executeIntVal, escapeString)
        .build();
    final ContractTxReceipt executionReceipt = execute(key, execution);

    // then
    assertEquals("SUCCESS", executionReceipt.getStatus());
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function(queryFunction)
        .args(executeKey)
        .build();
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    final Data data = queryResult.bind(Data.class);
    assertEquals(executeIntVal, data.getIntVal());
    assertEquals(escapeString, data.getStringVal());
  }

  @Test
  public void shouldExecuteFailOnInvalidNonce() throws Exception {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(simplePayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    try {
      // when
      final String executeKey = randomUUID().toString();
      final int executeIntVal = randomUUID().toString().hashCode();
      final String executeStringVal = randomUUID().toString();
      final ContractInvocation execution = contractInterface.newInvocationBuilder()
          .function(executeFunction)
          .args(executeKey, executeIntVal, executeStringVal)
          .build();
      aergoClient.getContractOperation().execute(key, execution, 0L, Fee.ZERO);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldSubscribeOnExecute() {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withEventPayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final EventFilter eventFilter = EventFilter.newBuilder(contractInterface.getAddress())
        .recentBlockCount(100)
        .build();
    final int count = 3;
    final AtomicInteger countDown = new AtomicInteger(count);
    final Subscription<Event> subscription =
        aergoClient.getContractOperation().subscribeEvent(eventFilter,
            new StreamObserver<Event>() {

              @Override
              public void onNext(Event value) {
                countDown.decrementAndGet();
              }

              @Override
              public void onError(Throwable t) {
                // do nothing
              }

              @Override
              public void onCompleted() {
                // do nothing
              }
            });
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();

    for (int i = 0; i < count; ++i) {
      execute(key, execution);
    }
    subscription.unsubscribe();

    // then
    assertTrue(subscription.isUnsubscribed());
    assertEquals(0, countDown.get());
  }

  @Test
  public void shouldNotCallBackOnUnsubscribedOne() {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withEventPayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final EventFilter eventFilter = EventFilter.newBuilder(contractInterface.getAddress())
        .recentBlockCount(100)
        .build();
    final int count = 3;
    final AtomicInteger countDown = new AtomicInteger(count);
    final Subscription<Event> subscription =
        aergoClient.getContractOperation().subscribeEvent(eventFilter,
            new StreamObserver<Event>() {

              @Override
              public void onNext(Event value) {
                countDown.decrementAndGet();
              }

              @Override
              public void onError(Throwable t) {
                // do nothing
              }

              @Override
              public void onCompleted() {
                // do nothing
              }
            });
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();

    for (int i = 0; i < count; ++i) {
      execute(key, execution);
    }
    subscription.unsubscribe();
    execute(key, execution);

    // then
    assertTrue(subscription.isUnsubscribed());
    assertEquals(0, countDown.get());
  }

  @Test
  public void shouldFilterEventWithNameOnSubscription() {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withEventPayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final EventFilter eventFilter = EventFilter.newBuilder(contractInterface.getAddress())
        .eventName(executeFunction)
        .build();
    final int count = 3;
    final AtomicInteger countDown = new AtomicInteger(count);
    final Subscription<Event> subscription =
        aergoClient.getContractOperation().subscribeEvent(eventFilter,
            new StreamObserver<Event>() {

              @Override
              public void onNext(Event value) {
                countDown.decrementAndGet();
              }

              @Override
              public void onError(Throwable t) {
                // do nothing
              }

              @Override
              public void onCompleted() {
                // do nothing
              }
            });

    final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
        .function(executeFunction2)
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    for (int i = 0; i < count; ++i) {
      execute(key, targetExec);
    }
    for (int i = 0; i < count - 1; ++i) {
      execute(key, otherExec);
    }
    subscription.unsubscribe();

    // then
    assertTrue(subscription.isUnsubscribed());
    assertEquals(0, countDown.get());
  }

  @Test
  public void shouldFilterEventWithArgsOnSubscription() {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withEventPayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String targetArg = randomUUID().toString();
    final EventFilter eventFilter = EventFilter.newBuilder(contractInterface.getAddress())
        .args(targetArg)
        .build();
    final int count = 3;
    final AtomicInteger countDown = new AtomicInteger(count);
    final Subscription<Event> subscription =
        aergoClient.getContractOperation().subscribeEvent(eventFilter,
            new StreamObserver<Event>() {

              @Override
              public void onNext(Event value) {
                countDown.decrementAndGet();
              }

              @Override
              public void onError(Throwable t) {
                // do nothing
              }

              @Override
              public void onCompleted() {
                // do nothing
              }
            });

    final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(targetArg, randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
        .function(executeFunction2)
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    for (int i = 0; i < count; ++i) {
      execute(key, targetExec);
    }
    for (int i = 0; i < count - 1; ++i) {
      execute(key, otherExec);
    }
    subscription.unsubscribe();

    // then
    assertTrue(subscription.isUnsubscribed());
    assertEquals(0, countDown.get());
  }

  @Test
  public void shouldFilterEventWithNameOnList() {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withEventPayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final EventFilter eventFilter = EventFilter.newBuilder(contractInterface.getAddress())
        .eventName(executeFunction)
        .recentBlockCount(1000)
        .build();
    final int count = 3;
    final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
        .function(executeFunction2)
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    for (int i = 0; i < count; ++i) {
      execute(key, targetExec);
    }
    for (int i = 0; i < count - 1; ++i) {
      execute(key, otherExec);
    }

    // then
    final List<Event> events = aergoClient.getContractOperation().listEvents(eventFilter);
    assertEquals(count, events.size());
  }

  @Test
  public void shouldFilterEventWithArgsOnList() {
    // given
    final AergoKey key = createNewKey();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(withEventPayload)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String targetArg = randomUUID().toString();
    final EventFilter eventFilter = EventFilter.newBuilder(contractInterface.getAddress())
        .args(targetArg)
        .recentBlockCount(1000)
        .build();
    final int count = 3;
    final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(targetArg, randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
        .function(executeFunction2)
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    for (int i = 0; i < count; ++i) {
      execute(key, targetExec);
    }
    for (int i = 0; i < count - 1; ++i) {
      execute(key, otherExec);
    }

    // then
    final List<Event> events = aergoClient.getContractOperation().listEvents(eventFilter);
    assertEquals(count, events.size());
  }



  @ToString
  protected static class Data {

    @Getter
    @Setter
    protected int intVal;

    @Getter
    @Setter
    protected String stringVal;
  }

  protected ContractInterface deployAndGetAbi(final Signer signer,
      final ContractDefinition definition) {
    final ContractTxReceipt receipt = deploy(signer, definition);
    return getAbi(receipt);
  }

  protected ContractTxReceipt deploy(final Signer signer,
      final ContractDefinition definition) {
    final ContractTxHash contractTxHash = aergoClient.getContractOperation().deploy(signer,
        definition, nonceProvider.incrementAndGetNonce(signer.getPrincipal()), Fee.getDefaultFee());

    waitForNextBlockToGenerate();

    return aergoClient.getContractOperation().getReceipt(contractTxHash);
  }

  protected ContractInterface redeployAndGetAbi(final Signer signer,
      final ContractAddress contractAddress, final ContractDefinition definition) {
    final ContractTxReceipt receipt = redeploy(signer, contractAddress, definition);
    return getAbi(receipt);
  }

  protected ContractTxReceipt redeploy(final Signer signer, final ContractAddress contractAddress,
      final ContractDefinition definition) {
    final ContractTxHash contractTxHash =
        aergoClient.getContractOperation().redeploy(signer, contractAddress, definition,
            nonceProvider.incrementAndGetNonce(signer.getPrincipal()), Fee.getDefaultFee());

    waitForNextBlockToGenerate();

    return aergoClient.getContractOperation().getReceipt(contractTxHash);
  }

  protected ContractInterface getAbi(final ContractTxReceipt receipt) {
    return aergoClient.getContractOperation().getContractInterface(receipt.getContractAddress());
  }

  protected ContractTxReceipt execute(final Signer signer, final ContractInvocation execution) {
    final ContractTxHash contractTxHash = aergoClient.getContractOperation().execute(signer,
        execution, nonceProvider.incrementAndGetNonce(signer.getPrincipal()), Fee.getDefaultFee());

    waitForNextBlockToGenerate();

    return aergoClient.getContractOperation().getReceipt(contractTxHash);
  }

}
