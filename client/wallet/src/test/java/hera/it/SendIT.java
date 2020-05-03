/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Name;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.exception.HerajException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.model.KeyAlias;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SendIT extends AbstractWalletApiIT {

  protected static AergoClient aergoClient;

  protected final Fee fee = Fee.ZERO;
  protected final AergoKey rich = AergoKey
      .of("47ExozzhsfEEVp2yhvNGxZxGLXPccRSdBydQeuJ5tmUpBij2M9gTSg2AESV83mGXGvu2U8bPR", "1234");
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
    final AergoKey key = new AergoKeyGenerator().create();
    final KeyAlias alias = new KeyAlias(randomUUID().toString().replace("-", ""));
    authentication = Authentication.of(alias, randomUUID().toString());
    keyStore.save(authentication, key);

    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY,
            BytesValue.EMPTY);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldSendAergo() {
    // when
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    walletApi.unlock(authentication);
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.GIGA_ONE;
    final BytesValue payload = BytesValue.of(randomUUID().toString().getBytes());
    final TxHash txHash = walletApi.with(aergoClient).transaction()
        .send(recipient, amount, fee, payload);
    waitForNextBlockToGenerate();

    // then
    final AccountState actual = walletApi.with(aergoClient).query()
        .getAccountState(recipient);
    assertEquals(amount, actual.getBalance());
    final Transaction transaction = walletApi.with(aergoClient).query()
        .getTransaction(txHash);
    assertEquals(payload, transaction.getPayload());
    final TxReceipt txReceipt = walletApi.with(aergoClient).query().getTxReceipt(txHash);
    assertEquals(recipient, txReceipt.getAccountAddress());
    assertEquals("SUCCESS", txReceipt.getStatus());
    assertEquals(txHash, txReceipt.getTxHash());
  }

  @Test
  public void shouldSendAergoFailOnLocked() {
    // when
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    final Aer amount = Aer.GIGA_ONE;
    final BytesValue payload = BytesValue.of(randomUUID().toString().getBytes());

    // then
    try {
      walletApi.with(aergoClient).transaction().send(recipient, amount, fee, payload);
      fail();
    } catch (HerajException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldSendAergoWithName() {
    // given
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    walletApi.unlock(authentication);
    final Name name = randomName();
    walletApi.with(aergoClient).transaction().createName(name);
    waitForNextBlockToGenerate();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    walletApi.with(aergoClient).transaction().updateName(name, recipient);
    waitForNextBlockToGenerate();
    walletApi.lock();

    // when
    walletApi.unlock(authentication);
    final Aer amount = Aer.GIGA_ONE;
    final BytesValue payload = BytesValue.of(randomName().toString().getBytes());
    final TxHash txHash = walletApi.with(aergoClient).transaction()
        .send(name, amount, fee, payload);
    waitForNextBlockToGenerate();

    // then
    final AccountState actual = walletApi.with(aergoClient).query()
        .getAccountState(recipient);
    assertEquals(amount, actual.getBalance());
    final Transaction transaction = walletApi.with(aergoClient).query()
        .getTransaction(txHash);
    assertEquals(payload, transaction.getPayload());
    final TxReceipt txReceipt = walletApi.with(aergoClient).query().getTxReceipt(txHash);
    assertEquals(recipient, txReceipt.getAccountAddress());
    assertEquals("SUCCESS", txReceipt.getStatus());
    assertEquals(txHash, txReceipt.getTxHash());
  }

  @Test
  public void shouldSendAergoWithNameFailOnLocked() {
    // given
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    walletApi.unlock(authentication);
    final Name name = randomName();
    walletApi.with(aergoClient).transaction().createName(name);
    waitForNextBlockToGenerate();
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();
    walletApi.with(aergoClient).transaction().updateName(name, recipient);
    waitForNextBlockToGenerate();
    walletApi.lock();

    // when
    final Aer amount = Aer.GIGA_ONE;
    final BytesValue payload = BytesValue.of(randomName().toString().getBytes());

    // then
    try {
      walletApi.with(aergoClient).transaction().send(name, amount, fee, payload);
      fail();
    } catch (HerajException e) {
      // good we expected this
    }
  }

}
