/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.StreamObserver;
import hera.api.model.Transaction;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.IoUtils;
import hera.wallet.Wallet;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LegacyContractIT extends AbstractLegacyWalletIT {

  protected static AergoClient aergoClient;

  protected final String deployKey = randomUUID().toString();
  protected final int deployIntVal = randomUUID().toString().hashCode();
  protected final String deployStringVal = randomUUID().toString();
  protected final String executeFunction = "set";
  protected final String executeKey = randomUUID().toString();
  protected final int executeIntVal = randomUUID().toString().hashCode();
  protected final String executeStringVal = randomUUID().toString();
  protected final String queryFunction = "get";
  protected final Fee fee = Fee.ZERO;
  protected final TestClientFactory clientFactory = new TestClientFactory();
  protected final AergoKey rich = AergoKey
      .of("47iciVrxLt5fdQvjFZeL7R3N69NMqPfPN5165hDQQXznvERfFe7nXW3ht2kPamre2WSsRHZwG", "1234");
  protected AergoKey key;

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

    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);;
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

  protected ContractInterface deploy(final Wallet wallet) throws IOException {
    final String encodedContract = IoUtils.from(new InputStreamReader(open("payload")));

    final long preCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState preState = wallet.getAccountState();

    final ContractDefinition contractDefinition = ContractDefinition.newBuilder()
        .encodedContract(encodedContract)
        .constructorArgs(deployKey, deployIntVal, deployStringVal)
        .build();

    final ContractTxHash deployTxHash = wallet.deploy(contractDefinition);
    waitForNextBlockToGenerate();

    final long postCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState postState = wallet.getAccountState();
    validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

    final ContractAddress contractAddress = wallet.getReceipt(deployTxHash).getContractAddress();
    return wallet.getContractInterface(contractAddress);
  }

  protected void execute(final Wallet wallet, final ContractInterface contractInterface) {
    final long preCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState preState = wallet.getAccountState();

    final ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(executeKey, executeIntVal, executeStringVal)
        .build();

    wallet.execute(contractInvocation);
    waitForNextBlockToGenerate();

    final long postCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState postState = wallet.getAccountState();
    validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);
  }

  protected void query(final Wallet wallet, final ContractInterface contractInterface)
      throws IOException {
    final long preCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState preState = wallet.getAccountState();

    final ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
        .function(queryFunction)
        .args(executeKey)
        .build();

    final Data data = wallet.query(contractInvocation).bind(Data.class);

    final long postCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState postState = wallet.getAccountState();
    validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 0);

    assertEquals(data.getIntVal(), executeIntVal);
    assertEquals(data.getStringVal(), executeStringVal);
  }

  @Test
  public void shouldDeployAndExecuteContractOnUnlocked() throws Exception {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    try {
      // when
      wallet.unlock(auth);
      final ContractInterface contractInterface = deploy(wallet);
      execute(wallet, contractInterface);

      // then
      query(wallet, contractInterface);
    } finally {
      wallet.lock(auth);
    }
  }

  @Test
  public void shouldDeployFailOnLocked() throws Exception {
    try {
      // when
      final String password = randomUUID().toString();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);
      wallet.lock(auth);
      deploy(wallet);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldExecuteFailOnLocked() throws Exception {
    try {
      // when
      final String password = randomUUID().toString();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);
      final ContractInterface contractInterface = deploy(wallet);
      wallet.lock(auth);
      execute(wallet, contractInterface);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void testContractEvent() throws Exception {
    // given
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication auth = Authentication.of(key.getAddress(), password);
    wallet.unlock(auth);
    final ContractInterface contractInterface = deploy(wallet);
    final ContractAddress contractAddress = contractInterface.getAddress();

    // when
    final int eventCount = 2;
    final CountDownLatch latch = new CountDownLatch(eventCount);
    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
        .recentBlockCount(10)
        .build();
    wallet.subscribeEvent(eventFilter, new StreamObserver<Event>() {

      @Override
      public void onNext(Event value) {
        latch.countDown();
      }

      @Override
      public void onError(Throwable t) {}

      @Override
      public void onCompleted() {}
    });
    for (int i = 0; i < eventCount; ++i) {
      execute(wallet, contractInterface);
    }

    // then
    assertEquals(0L, latch.getCount());
    List<Event> events = wallet.listEvents(eventFilter);
    assertEquals(eventCount, events.size());
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
