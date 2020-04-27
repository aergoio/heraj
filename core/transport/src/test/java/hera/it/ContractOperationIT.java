/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
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
import hera.api.model.TxHash;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContractOperationIT extends AbstractIT {

  protected static AergoClient aergoClient;

  protected final Map<String, String> payloadMap = new HashMap<>();
  protected final Fee fee = Fee.ZERO;
  protected final NonceProvider nonceProvider = new SimpleNonceProvider();
  protected final AergoKey rich = AergoKey
      .of("47sbh237YhZePchrES9q5Gkooczkz7NLZL6ARvhUEiG25MPmm9c3Moth2FRFYhzKmzFR1nqUw", "1234");
  protected AergoKey key;

  {
    try {
      payloadMap.put("simple_payload", IoUtils.from(new InputStreamReader(open("simple_payload"))));
      payloadMap.put("with_payable_payload",
          IoUtils.from(new InputStreamReader(open("with_payable_payload"))));
      payloadMap.put("with_abi_added_payload",
          IoUtils.from(new InputStreamReader(open("with_abi_added_payload"))));
      payloadMap.put("with_event_payload",
          IoUtils.from(new InputStreamReader(open("with_event_payload"))));
      payloadMap.put("with_bignum_payload",
          IoUtils.from(new InputStreamReader(open("with_bignum_payload"))));
      payloadMap.put("with_event_nested_args_payload",
          IoUtils.from(new InputStreamReader(open("with_event_args_payload"))));
      payloadMap.put("with_fee_delegation_payload",
          IoUtils.from(new InputStreamReader(open("with_fee_delegation_payload"))));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @BeforeClass
  public static void before() {
    final TestClientFactory clientFactory = new TestClientFactory();
    aergoClient = clientFactory.get();
  }

  @AfterClass
  public static void after() throws Exception {
    aergoClient.close();
  }

  @Before
  public void setUp() {
    key = new AergoKeyGenerator().create();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldDeployOnPlainContract() throws Exception {
    // when
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .build();
    final ContractTxReceipt receipt = deploy(key, definition);

    // then
    assertEquals("CREATED", receipt.getStatus());
  }

  @Test
  public void shouldApplyConstructorArgsOnDeploy() throws Exception {
    // when
    final String deployKey = randomUUID().toString();
    final int deployIntVal = randomUUID().toString().hashCode();
    final String deployStringVal = randomUUID().toString();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .constructorArgs(deployKey, deployIntVal, deployStringVal)
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // then
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function("get")
        .args(deployKey)
        .build();
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    final Data data = queryResult.bind(Data.class);
    assertEquals(deployIntVal, data.getIntVal());
    assertEquals(deployStringVal, data.getStringVal());
  }

  @Test
  public void shouldDeployFailOnInvaidNonce() throws Exception {
    try {
      // when
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(payloadMap.get("simple_payload"))
          .build();
      aergoClient.getContractOperation().deployTx(key, definition, 0L, fee);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldReDeployOnDeployedContract() throws Exception {
    // given
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .build();
    final ContractTxReceipt deployReceipt = deploy(key, definition);
    final ContractAddress originAddress = deployReceipt.getContractAddress();

    // when
    final String reDeployKey = randomUUID().toString();
    final int reDeployIntVal = randomUUID().toString().hashCode();
    final String reDeployStringVal = randomUUID().toString();
    final ContractDefinition newDefinition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_abi_added_payload"))
        .constructorArgs(reDeployKey, reDeployIntVal, reDeployStringVal)
        .build();
    final ContractTxReceipt reDeployTxReceipt = redeploy(key, originAddress, newDefinition);

    // then
    assertEquals("RECREATED", reDeployTxReceipt.getStatus());
    assertEquals(originAddress, reDeployTxReceipt.getContractAddress());
    final ContractInterface contractInterface = getAbi(reDeployTxReceipt);
    final ContractFunction newQueryFunc = contractInterface.findFunction("newGet");
    assertNotNull(newQueryFunc);
  }

  @Test
  public void shouldApplyConstructorArgsOnReDeploy() throws Exception {
    // given
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .build();
    final ContractTxReceipt deployReceipt = deploy(key, definition);
    final ContractAddress originAddress = deployReceipt.getContractAddress();

    // when
    final String reDeployKey = randomUUID().toString();
    final int reDeployIntVal = randomUUID().toString().hashCode();
    final String reDeployStringVal = randomUUID().toString();
    final ContractDefinition newDefinition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_abi_added_payload"))
        .constructorArgs(reDeployKey, reDeployIntVal, reDeployStringVal)
        .build();
    final ContractInterface contractInterface =
        redeployAndGetAbi(key, originAddress, newDefinition);

    // then
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function("newGet")
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
    final String deployKey = randomUUID().toString();
    final int deployIntVal = randomUUID().toString().hashCode();
    final String deployStringVal = randomUUID().toString();
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .constructorArgs(deployKey, deployIntVal, deployStringVal)
        .build();
    final ContractTxReceipt deployReceipt = deploy(key, definition);
    final ContractAddress originAddress = deployReceipt.getContractAddress();

    // when
    final ContractDefinition newDefinition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_abi_added_payload"))
        .build();
    final ContractInterface contractInterface =
        redeployAndGetAbi(key, originAddress, newDefinition);

    // then
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function("newGet")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .build();
    final ContractTxReceipt deployReceipt = deploy(key, definition);
    final ContractAddress originAddress = deployReceipt.getContractAddress();

    // when
    final ContractDefinition newDefinition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_abi_added_payload"))
        .build();
    final ContractInterface contractInterface =
        redeployAndGetAbi(key, originAddress, newDefinition);

    // then
    final ContractFunction newQueryFunc = contractInterface.findFunction("newGet");
    assertNotNull(newQueryFunc);
  }

  @Test
  public void shouldHasAmountSameAsDeployedOne() throws Exception {
    // when
    final Aer expected = Aer.GIGA_ONE;
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_payable_payload"))
        .amount(expected)
        .build();
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final int executeIntVal = randomUUID().toString().hashCode();
    final String executeStringVal = randomUUID().toString();
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args(executeKey, executeIntVal, executeStringVal)
        .build();
    final ContractTxReceipt executionReceipt = execute(key, execution);

    // then
    assertEquals("SUCCESS", executionReceipt.getStatus());
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function("get")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final int executeIntVal = randomUUID().toString().hashCode();
    final String executeStringVal = randomUUID().toString();
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_bignum_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final BigNumber x = new BigNumber("111");
    final BigNumber y = new BigNumber("222");
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args(executeKey, x, y)
        .build();
    execute(key, execution);

    // then
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function("get")
        .args(executeKey)
        .build();
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    final BigNumber actual = queryResult.bind(BigNumber.class);
    assertEquals(x.add(y), actual);
  }

  @Test
  public void shouldHasAmountSameAsUsedInExecution() throws Exception {
    // given
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_payable_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final int executeIntVal = randomUUID().toString().hashCode();
    final String executeStringVal = randomUUID().toString();
    final Aer expected = Aer.GIGA_ONE;
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final String executeKey = randomUUID().toString();
    final int executeIntVal = randomUUID().toString().hashCode();
    final String escapeString = "\b\t\f\n\r";
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args(executeKey, executeIntVal, escapeString)
        .build();
    final ContractTxReceipt executionReceipt = execute(key, execution);

    // then
    assertEquals("SUCCESS", executionReceipt.getStatus());
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function("get")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("simple_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    try {
      // when
      final String executeKey = randomUUID().toString();
      final int executeIntVal = randomUUID().toString().hashCode();
      final String executeStringVal = randomUUID().toString();
      final ContractInvocation execution = contractInterface.newInvocationBuilder()
          .function("set")
          .args(executeKey, executeIntVal, executeStringVal)
          .build();
      aergoClient.getContractOperation().executeTx(key, execution, 0L, fee);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldExecuteUseSenderFeeOnNotFeeDelegation() throws Exception {
    // given
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_fee_delegation_payload"))
        .amount(Aer.of("10", Unit.AERGO))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final AccountState preState = aergoClient.getAccountOperation().getState(key.getAddress());
    final String executeKey = randomUUID().toString();
    final String executeValue = randomUUID().toString();
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args(executeKey, executeValue)
        .delegateFee(false)
        .build();
    final ContractTxReceipt txReceipt = execute(key, execution);

    // then
    final AccountState afterState = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer preBalancePlusUsedFee = afterState.getBalance().add(txReceipt.getFeeUsed());
    assertEquals(preState.getBalance(), preBalancePlusUsedFee);
  }

  @Test
  public void shouldExecuteNotUseSenderFeeOnFeeDelegation() throws Exception {
    // given
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_fee_delegation_payload"))
        .amount(Aer.of("10", Unit.AERGO))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final AccountState preState = aergoClient.getAccountOperation().getState(key.getAddress());
    final String executeKey = randomUUID().toString();
    final String executeValue = randomUUID().toString();
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function("set")
        .args(executeKey, executeValue)
        .delegateFee(true)
        .build();
    execute(key, execution);

    // then
    final AccountState afterState = aergoClient.getAccountOperation().getState(key.getAddress());
    assertEquals(preState.getBalance(), afterState.getBalance());
  }

  @Test
  public void shouldSubscribeOnExecute() {
    // given
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_event_payload"))
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
        .function("set")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_event_payload"))
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
        .function("set")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_event_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final EventFilter eventFilter = EventFilter.newBuilder(contractInterface.getAddress())
        .eventName("set")
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
        .function("set")
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
        .function("set2")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_event_payload"))
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
        .function("set")
        .args(targetArg, randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
        .function("set2")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_event_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final EventFilter eventFilter = EventFilter.newBuilder(contractInterface.getAddress())
        .eventName("set")
        .recentBlockCount(1000)
        .build();
    final int count = 3;
    final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
        .function("set")
        .args(randomUUID().toString(), randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
        .function("set2")
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
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_event_payload"))
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
        .function("set")
        .args(targetArg, randomUUID().toString().hashCode(), randomUUID().toString())
        .build();
    final ContractInvocation otherExec = contractInterface.newInvocationBuilder()
        .function("set2")
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
  public void shouldParseEventArgsInLuaTypes() {
    // given
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payloadMap.get("with_event_nested_args_payload"))
        .build();
    final ContractInterface contractInterface = deployAndGetAbi(key, definition);

    // when
    final ContractInvocation targetExec = contractInterface.newInvocationBuilder()
        .function("throwEvent")
        .build();
    final ContractTxReceipt receipt = execute(key, targetExec);

    // then
    final List<Event> events = receipt.getEvents();
    final Event simpleArgs = events.get(0);
    final Map<String, Object> simpleJson = new HashMap<>();
    simpleJson.put("key", "value");
    final List<Object> expectedSimpleArgs = asList(
        simpleJson,
        "text",
        123.123,
        123,
        true);
    assertEquals(expectedSimpleArgs, simpleArgs.getArgs());

    // and then
    final Event nestedArgs = events.get(1);
    final Map<String, Object> inner = new HashMap<>();
    inner.put("innerKey", "123");
    final Map<String, Object> expectedNested = new HashMap<>();
    expectedNested.put("key", inner);
    assertEquals(expectedNested, nestedArgs.getArgs().get(0));

    // and then
    final Event bignumArgs = events.get(2);
    final BigNumber expectedBignum = new BigNumber("123");
    assertEquals(expectedBignum, bignumArgs.getArgs().get(0));
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
    final ContractTxHash contractTxHash = aergoClient.getContractOperation().deployTx(signer,
        definition, nonceProvider.incrementAndGetNonce(signer.getPrincipal()), fee);

    waitForNextBlockToGenerate();

    final ContractTxReceipt receipt = aergoClient.getContractOperation()
        .getContractTxReceipt(contractTxHash);
    assertNotEquals("ERROR", receipt.getStatus());
    return receipt;
  }

  protected ContractInterface redeployAndGetAbi(final Signer signer,
      final ContractAddress contractAddress, final ContractDefinition definition) {
    final ContractTxReceipt receipt = redeploy(signer, contractAddress, definition);
    return getAbi(receipt);
  }

  protected ContractTxReceipt redeploy(final Signer signer, final ContractAddress contractAddress,
      final ContractDefinition definition) {
    final ContractTxHash contractTxHash =
        aergoClient.getContractOperation().redeployTx(signer, contractAddress, definition,
            nonceProvider.incrementAndGetNonce(signer.getPrincipal()), fee);

    waitForNextBlockToGenerate();

    final ContractTxReceipt receipt = aergoClient.getContractOperation()
        .getContractTxReceipt(contractTxHash);
    assertNotEquals("ERROR", receipt.getStatus());
    return receipt;
  }

  protected ContractInterface getAbi(final ContractTxReceipt receipt) {
    return aergoClient.getContractOperation().getContractInterface(receipt.getContractAddress());
  }

  protected ContractTxReceipt execute(final Signer signer, final ContractInvocation execution) {
    final ContractTxHash contractTxHash = aergoClient.getContractOperation().executeTx(signer,
        execution, nonceProvider.incrementAndGetNonce(signer.getPrincipal()), fee);

    waitForNextBlockToGenerate();

    final ContractTxReceipt receipt = aergoClient.getContractOperation()
        .getContractTxReceipt(contractTxHash);
    assertNotEquals("ERROR", receipt.getStatus());
    return receipt;
  }

}
