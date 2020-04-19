/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.exception.HerajException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.model.KeyAlias;
import hera.util.IoUtils;
import hera.util.Pair;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContractIT extends AbstractWalletApiIT {

  protected static AergoClient aergoClient;

  protected final String execFunction = "set";
  protected final String execFunctionEvent = "set";
  protected final String execFunction2 = "set2";
  protected final String execFunction2Event = "set2";
  protected final String queryFunction = "get";

  protected final Fee fee = Fee.ZERO;
  protected final NonceProvider nonceProvider = new SimpleNonceProvider();
  protected final AergoKey rich = AergoKey
      .of("47fNiWbgirRnXqy26PtnZwQDevn6EEwHn7dvUD2agE3YooXWPD7YpzTGQaaxLUjmC59abDSSi", "1234");
  protected WalletApi walletApi;
  protected Authentication authentication;

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
    walletApi = new WalletApiFactory().create(keyStore);
    walletApi.bind(aergoClient);

    final AergoKey key = new AergoKeyGenerator().create();
    final KeyAlias alias = new KeyAlias(randomUUID().toString().replace("-", ""));
    authentication = Authentication.of(alias, randomUUID().toString());
    keyStore.save(authentication, key);

    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    ;
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(rich.getPrincipal())
        .to(key.getAddress())
        .amount(Aer.of("10000", Unit.AERGO))
        .nonce(nonceProvider.incrementAndGetNonce(rich.getPrincipal()))
        .build();
    final Transaction signed = rich.sign(rawTransaction);
    logger.debug("Fill tx: ", signed);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldDeployOnUnlocked() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));

    // then
    final ContractInterface contractInterface = deploy(payload, fee);
    assertNotNull(contractInterface);
  }

  @Test
  public void shouldDeployFailOnLocked() throws IOException {
    // when
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));

    // then
    try {
      deploy(payload, fee);
      fail();
    } catch (HerajException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldValueSetOnDeployWithArgs() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final String key = randomUUID().toString();
    final int intVal = randomUUID().hashCode();
    final String stringVal = randomUUID().toString();

    // then
    final ContractInterface contractInterface = deploy(payload, fee, key, intVal, stringVal);
    final ContractResult result = query(contractInterface, queryFunction, key);
    final Data data = result.bind(Data.class);
    assertEquals(intVal, data.getIntVal());
    assertEquals(stringVal, data.getStringVal());
  }

  @Test
  public void shouldExecuteOnUnlocked() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final ContractInterface contractInterface = deploy(payload, fee);

    // then
    final String key = randomUUID().toString();
    final int intVal = randomUUID().hashCode();
    final String stringVal = randomUUID().toString();
    execute(contractInterface, execFunction, fee, key, intVal, stringVal);
    final ContractResult result = query(contractInterface, queryFunction, key);
    final Data data = result.bind(Data.class);
    assertEquals(intVal, data.getIntVal());
    assertEquals(stringVal, data.getStringVal());
  }

  @Test
  public void shouldExecuteFailOnLocked() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final ContractInterface contractInterface = deploy(payload, fee);
    walletApi.lock(authentication);

    // then
    final String key = randomUUID().toString();
    final int intVal = randomUUID().hashCode();
    final String stringVal = randomUUID().toString();
    try {
      execute(contractInterface, execFunction, fee, key, intVal, stringVal);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void shouldSubscribeExecEvent() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final ContractInterface contractInterface = deploy(payload, fee);

    final int tryCount = 5;
    final Collection<Pair<String, Pair<Integer, String>>> execArgs = generateArgs(tryCount);
    final EventFilter filter = EventFilter.newBuilder(contractInterface.getAddress())
        .build();
    final Subscription<Event> subsription =
        walletApi.queryApi().subscribeEvent(filter, new StreamObserver<Event>() {

          @Override
          public void onNext(Event value) {
            List<Object> rawArgs = value.getArgs();
            final Pair<String, Pair<Integer, String>> recovered = recoverArgs(rawArgs);
            execArgs.remove(recovered);
          }

          @Override
          public void onError(Throwable t) {
          }

          @Override
          public void onCompleted() {
          }
        });

    // then
    final Collection<Pair<String, Pair<Integer, String>>> clone = new HashSet<>(execArgs);
    for (final Pair<String, Pair<Integer, String>> next : clone) {
      execute(contractInterface, execFunction, fee, next.v1, next.v2.v1, next.v2.v2);
    }
    assertEquals(0, execArgs.size());
    subsription.unsubscribe();
  }

  @Test
  public void shouldFilterEventByFuncName() throws IOException {
    // when
    walletApi.unlock(authentication);
    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final ContractInterface contractInterface = deploy(payload, fee);

    final int event1Try = 2;
    final int event2Try = 4;

    final AtomicInteger count = new AtomicInteger(event1Try);
    final EventFilter filter = EventFilter.newBuilder(contractInterface.getAddress())
        .eventName(execFunctionEvent)
        .build();
    final Subscription<Event> subsription =
        walletApi.queryApi().subscribeEvent(filter, new StreamObserver<Event>() {

          @Override
          public void onNext(Event value) {
            count.decrementAndGet();
          }

          @Override
          public void onError(Throwable t) {
          }

          @Override
          public void onCompleted() {
          }
        });

    // then
    for (int i = 0; i < event1Try; ++i) {
      execute(contractInterface, execFunction, fee, randomUUID().toString(),
          randomUUID().hashCode(), randomUUID().toString());
    }
    for (int i = 0; i < event2Try; ++i) {
      execute(contractInterface, execFunction2, fee, randomUUID().toString(),
          randomUUID().hashCode(), randomUUID().toString());
    }
    assertEquals(0, count.get());
    subsription.unsubscribe();

  }


  protected Collection<Pair<String, Pair<Integer, String>>> generateArgs(final int tryCount) {
    final Collection<Pair<String, Pair<Integer, String>>> execArgs = new HashSet<>();
    for (int i = 0; i < tryCount; ++i) {
      final Pair<Integer, String> value =
          new Pair<>(randomUUID().hashCode(), randomUUID().toString());
      final Pair<String, Pair<Integer, String>> keyAndValue =
          new Pair<>(randomName().toString(), value);
      execArgs.add(keyAndValue);
    }
    return execArgs;
  }

  protected Pair<String, Pair<Integer, String>> recoverArgs(List<Object> rawArgs) {
    final Pair<Integer, String> recoveredValue =
        new Pair<>((Integer) rawArgs.get(1), (String) rawArgs.get(2));
    final Pair<String, Pair<Integer, String>> recoveredKeyAndValue =
        new Pair<>((String) rawArgs.get(0), recoveredValue);
    return recoveredKeyAndValue;
  }

  protected ContractInterface deploy(final String payload, final Fee fee, final Object... args) {
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .constructorArgs(args)
        .build();
    final ContractTxHash deployTxHash = walletApi.transactionApi().deploy(definition, fee);
    waitForNextBlockToGenerate();

    final ContractTxReceipt receipt = walletApi.queryApi().getReceipt(deployTxHash);
    assertNotEquals("ERROR", receipt.getStatus());
    final ContractAddress contractAddress = receipt.getContractAddress();
    final ContractInterface contractInterface =
        walletApi.queryApi().getContractInterface(contractAddress);
    return contractInterface;
  }

  protected ContractTxReceipt execute(final ContractInterface contractInterface,
      final String funcName,
      final Fee fee, final Object... args) {
    final ContractInvocation execution = contractInterface.newInvocationBuilder()
        .function(funcName)
        .args(args)
        .build();
    final ContractTxHash execTxHash = walletApi.transactionApi().execute(execution, fee);
    waitForNextBlockToGenerate();
    final ContractTxReceipt receipt = walletApi.queryApi().getReceipt(execTxHash);
    assertNotEquals("ERROR", receipt.getStatus());
    return receipt;
  }

  protected ContractResult query(final ContractInterface contractInterface, final String funcName,
      final Object... args) {
    final ContractInvocation query = contractInterface.newInvocationBuilder()
        .function(funcName)
        .args(args)
        .build();
    return walletApi.queryApi().query(query);
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

}
