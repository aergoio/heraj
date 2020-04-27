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
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.contract.ContractApi;
import hera.contract.ContractApiFactory;
import hera.exception.HerajException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.model.KeyAlias;
import hera.util.IoUtils;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import java.io.InputStreamReader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContractApiIT extends AbstractIT {

  protected static AergoClient aergoClient;

  protected Fee fee = Fee.ZERO;
  protected final TestClientFactory clientFactory = new TestClientFactory();
  protected final AergoKey rich = AergoKey
      .of("47ExozzhsfEEVp2yhvNGxZxGLXPccRSdBydQeuJ5tmUpBij2M9gTSg2AESV83mGXGvu2U8bPR", "1234");
  protected AergoKey key;
  protected WalletApi walletApi;
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

    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY);
    waitForNextBlockToGenerate();

    // create wallet api
    final KeyStore keyStore = new InMemoryKeyStore();
    this.walletApi = new WalletApiFactory().create(keyStore);
    this.walletApi.bind(aergoClient);

    // save
    final KeyAlias alias = new KeyAlias(randomUUID().toString().replace("-", ""));
    final Authentication authentication = Authentication.of(alias, randomUUID().toString());
    keyStore.save(authentication, key);
    this.walletApi.unlock(authentication);

    // deploy contract
    String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .build();
    final ContractTxHash deployTxHash = walletApi.transactionApi().deploy(definition, fee);
    waitForNextBlockToGenerate();
    this.contractAddress = walletApi.queryApi().getContractTxReceipt(deployTxHash).getContractAddress();
  }

  @Test
  public void testInvocation() {
    final ContractApi<ValidInterface> contractApi =
        new ContractApiFactory().create(contractAddress, ValidInterface.class);

    // final Object nilArg = null;
    final boolean booleanArg = true;
    final int numberArg = 20000;
    final String stringArg = randomUUID().toString();

    // contractApi.walletApi(walletApi).fee(fee).setNil(nilArg);
    contractApi.walletApi(walletApi).fee(fee).setBoolean(booleanArg);
    contractApi.walletApi(walletApi).fee(fee).setNumber(numberArg);
    contractApi.walletApi(walletApi).fee(fee).setString(stringArg);

    waitForNextBlockToGenerate();

    // assertNull(contractApi.walletApi(walletApi).noFee().getNil());
    assertEquals(booleanArg, contractApi.walletApi(walletApi).noFee().getBoolean());
    assertEquals(numberArg, contractApi.walletApi(walletApi).noFee().getNumber());
    assertEquals(stringArg, contractApi.walletApi(walletApi).noFee().getString());
  }

  @Test
  public void testInvocationOnInvalidMethodNameInterface() {
    try {
      // when
      final ContractApi<InvalidMethodNameInterface> contractApi = new ContractApiFactory()
          .create(contractAddress, InvalidMethodNameInterface.class);
      contractApi.walletApi(walletApi).fee(fee).getNill();
      fail();
    } catch (HerajException e) {
      // then
    }
  }

  public static interface ValidInterface {

    void setNil(Object nilArg);

    Object getNil();

    void setBoolean(boolean booleanArg);

    boolean getBoolean();

    void setNumber(int numberArg);

    int getNumber();

    void setString(String stringArg);

    String getString();

  }

  public static interface InvalidMethodNameInterface {

    void setNil(Object nilArg);

    // invalid
    Object getNill();

    void setBoolean(boolean booleanArg);

    boolean getBoolean();

    void setNumber(int numberArg);

    int getNumber();

    void setString(String stringArg);

    String getString();

  }

}
