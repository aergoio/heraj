/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.contract.ContractApi;
import hera.contract.ContractApiFactory;
import hera.exception.HerajException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContractApiIT extends AbstractIT {

  protected static AergoClient aergoClient;

  protected final AergoKey rich = AergoKey
      .of("47ExozzhsfEEVp2yhvNGxZxGLXPccRSdBydQeuJ5tmUpBij2M9gTSg2AESV83mGXGvu2U8bPR", "1234");
  protected AergoKey key;
  protected NonceProvider nonceProvider = new SimpleNonceProvider();
  protected ContractAddress contractAddress;

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
  public void setUp() throws Exception {
    // fill aergo to key
    key = new AergoKeyGenerator().create();

    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY,
            BytesValue.EMPTY);
    waitForNextBlockToGenerate();

    // deploy contract
    String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .build();
    final ContractTxHash deployTxHash = aergoClient.getContractOperation()
        .deployTx(rich, definition, nonceProvider.incrementAndGetNonce(rich.getPrincipal()),
            Fee.INFINITY);
    waitForNextBlockToGenerate();

    // bind contract address
    this.contractAddress = aergoClient.getContractOperation().getContractTxReceipt(deployTxHash)
        .getContractAddress();
  }

  @Test
  public void shouldRunOnValidLuaType() {
    // given
    final ContractApi<LuaTypeInterface> contractApi = new ContractApiFactory()
        .create(contractAddress, LuaTypeInterface.class);
    final Signer signer = key;
    final boolean booleanArg = true;
    final int numberArg = 20000;
    final String stringArg = randomUUID().toString();
    // final Object nilArg = null;

    // when
    contractApi.with(aergoClient).execution(signer).setBoolean(booleanArg);
    contractApi.with(aergoClient).execution(signer).setNumber(numberArg);
    contractApi.with(aergoClient).execution(signer).setString(stringArg);
    // contractApi.walletApi(walletApi).fee(fee).setNil(nilArg);
    waitForNextBlockToGenerate();

    // then
    assertEquals(booleanArg, contractApi.with(aergoClient).query().getBoolean());
    assertEquals(numberArg, contractApi.with(aergoClient).query().getNumber());
    assertEquals(stringArg, contractApi.with(aergoClient).query().getString());
    // assertNull(contractApi.walletApi(walletApi).noFee().getNil());
  }

  @Test
  public void shouldThrowErrorOnInvalidMethodName() {
    try {
      // when
      final ContractApi<InvalidMethodNameInterface> contractApi = new ContractApiFactory()
          .create(contractAddress, InvalidMethodNameInterface.class);
      contractApi.with(aergoClient).query().getNill();
      fail();
    } catch (HerajException e) {
      // then
    }
  }

  @Test
  public void shouldGetTxHashOnTxHashReturnType() {
    // given
    final ContractApi<TestInterface> contractApi = new ContractApiFactory()
        .create(contractAddress, TestInterface.class);
    final Signer signer = key;
    final String stringArg = randomUUID().toString();

    // when
    final TxHash txHash = contractApi.with(aergoClient).execution(signer).setString(stringArg);
    waitForNextBlockToGenerate();

    // then
    final ContractTxReceipt contractTxReceipt = aergoClient.getContractOperation()
        .getContractTxReceipt(txHash.adapt(ContractTxHash.class));
    assertEquals(contractAddress, contractTxReceipt.getContractAddress());
  }

  @Test
  public void shouldPrepareFeeWhenFeeOnArguments() {
    // given
    final ContractApi<TestInterface> contractApi = new ContractApiFactory()
        .create(contractAddress, TestInterface.class);
    final Signer signer = key;
    final String stringArg = randomUUID().toString();

    // when
    final Fee expected = Fee.of(1000000L);
    final TxHash txHash = contractApi.with(aergoClient).execution(signer)
        .setString(stringArg, expected);
    waitForNextBlockToGenerate();

    // then
    final Transaction transaction = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertEquals(expected, transaction.getFee());
  }

  @Test
  public void shouldPrepareLastFeeWhenFeeOnArguments() {
    // given
    final ContractApi<TestInterface> contractApi = new ContractApiFactory()
        .create(contractAddress, TestInterface.class);
    final Signer signer = key;
    final String expectedStringArg = randomUUID().toString();

    // when
    final Fee dummy = Fee.of(1000L);
    final Fee expected = Fee.of(1000000L);
    final TxHash txHash = contractApi.with(aergoClient).execution(signer)
        .setString(dummy, expectedStringArg, expected);
    waitForNextBlockToGenerate();

    // then
    final Transaction transaction = aergoClient.getTransactionOperation().getTransaction(txHash);
    assertEquals(expected, transaction.getFee());
    final String actualStringArg = contractApi.with(aergoClient).query().getString();
    assertEquals(expectedStringArg, actualStringArg);
  }

  @Test
  public void testExecuteOnMultiThread() throws ExecutionException, InterruptedException {
    // given
    final ContractApi<TestInterface> contractApi = new ContractApiFactory()
        .create(contractAddress, TestInterface.class);
    final Signer signer = key;
    final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
    final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    // then
    final List<Future<TxHash>> futures = new ArrayList<>();
    for (int i = 0; i < nThreads * 100; ++i) {
      final Future<TxHash> future = executorService.submit(new Callable<TxHash>() {
        @Override
        public TxHash call() throws Exception {
          return contractApi.with(aergoClient).execution(signer).setString("test");
        }
      });
      futures.add(future);
    }
    for (final Future<TxHash> future : futures) {
      final TxHash txHash = future.get();
      assertNotNull(txHash);
    }
  }

  private interface LuaTypeInterface {

    void setNil(Object nilArg);

    Object getNil();

    void setBoolean(boolean booleanArg);

    boolean getBoolean();

    void setNumber(int numberArg);

    int getNumber();

    void setString(String stringArg);

    String getString();

  }

  private interface TestInterface {

    TxHash setString(String stringArg);

    TxHash setString(String stringArg, Fee fee);

    TxHash setString(Fee fee1, String stringArg, Fee fee2);

    String getString();

  }

  private interface InvalidMethodNameInterface {

    // invalid
    Object getNill();

  }

}
