/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.it;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.IoUtils;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class WalletIT extends AbstractIT {

  protected final AccountAddress accountAddress =
      AccountAddress.of("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  protected String keyStorePath = System.getProperty("java.io.tmpdir") + "/.keystore";
  protected String keyStorePasword = "password";

  protected String password = randomUUID().toString();

  protected String deployKey = randomUUID().toString();
  protected int deployIntVal = randomUUID().toString().hashCode();
  protected String deployStringVal = randomUUID().toString();

  protected String executeFunction = "set";
  protected String executeKey = randomUUID().toString();
  protected int executeIntVal = randomUUID().toString().hashCode();
  protected String executeStringVal = randomUUID().toString();

  protected String queryFunction = "get";

  protected java.security.KeyStore keyStore;



  @Parameters(name = "{0}")
  public static Collection<WalletType> data() {
    return Arrays.asList(
        WalletType.Naive,
        WalletType.Secure,
        WalletType.ServerKeyStore);
  }

  @Parameter
  public WalletType type;


  protected List<Wallet> supplyWorkingWalletList() {
    return asList(
        new WalletBuilder()
            .withTimeout(1000L, TimeUnit.MILLISECONDS)
            .withNonBlockingConnect()
            .withEndpoint(hostname)
            .build(type),
        new WalletBuilder()
            .withTimeout(1000L, TimeUnit.MILLISECONDS)
            .withBlockingConnect()
            .withEndpoint(hostname)
            .build(type),
        new WalletBuilder()
            .withTracking()
            .withTimeout(1000L, TimeUnit.MILLISECONDS)
            .withBlockingConnect()
            .withEndpoint(hostname)
            .build(type));
  }


  protected void validatePreAndPostState(final AccountState preState, final long preCachedNonce,
      final AccountState postState, final long postCachedNonce, final long requestCount) {
    assertEquals(preState.getNonce(), preCachedNonce);
    assertEquals(postState.getNonce(), postCachedNonce);
    assertEquals(preState.getNonce() + requestCount, postState.getNonce());
    assertEquals(preCachedNonce + requestCount, postCachedNonce);
  }

  protected ContractInterface deploy(final Wallet wallet) throws IOException {
    final String encodedContract = IoUtils.from(new InputStreamReader(open("payload")));

    final long preCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState preState = wallet.getCurrentAccountState();

    final ContractDefinition contractDefinition = ContractDefinition.newBuilder()
        .encodedContract(encodedContract)
        .constructorArgs(deployKey, deployIntVal, deployStringVal)
        .build();

    final ContractTxHash deployTxHash = wallet.deploy(contractDefinition, Fee.getDefaultFee());
    waitForNextBlockToGenerate();

    final long postCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState postState = wallet.getCurrentAccountState();
    validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

    final ContractAddress contractAddress = wallet.getReceipt(deployTxHash).getContractAddress();
    return wallet.getContractInterface(contractAddress);
  }

  protected void execute(final Wallet wallet, final ContractInterface contractInterface) {
    final long preCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState preState = wallet.getCurrentAccountState();

    final ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
        .function(executeFunction)
        .args(executeKey, executeIntVal, executeStringVal)
        .build();

    wallet.execute(contractInvocation, Fee.getDefaultFee());
    waitForNextBlockToGenerate();

    final long postCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState postState = wallet.getCurrentAccountState();
    validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);
  }

  protected void query(final Wallet wallet, final ContractInterface contractInterface)
      throws IOException {
    final long preCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState preState = wallet.getCurrentAccountState();

    final ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
        .function(queryFunction)
        .args(executeKey)
        .build();

    final Data data = wallet.query(contractInvocation).bind(Data.class);

    final long postCachedNonce = wallet.getRecentlyUsedNonce();
    final AccountState postState = wallet.getCurrentAccountState();
    validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 0);

    assertEquals(data.getIntVal(), executeIntVal);
    assertEquals(data.getStringVal(), executeStringVal);
  }

  @Before
  public void setUp() throws Exception {
    this.keyStore = supplyKeyStore();
  }

  protected java.security.KeyStore supplyKeyStore() throws Exception {
    final java.security.KeyStore keyStore = java.security.KeyStore.getInstance("PKCS12");
    try {
      keyStore.load(new FileInputStream(keyStorePath),
          keyStorePasword.toCharArray());
      return keyStore;
    } catch (CertificateException | IOException e) {
      keyStore.load(null, keyStorePasword.toCharArray());
    }
    return keyStore;
  }

  @After
  public void tearDown() throws Exception {
    keyStore.store(new FileOutputStream(new File(keyStorePath)), keyStorePasword.toCharArray());
  }

  @Test
  public void testLookup() throws Exception {
    for (Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      final String password = randomUUID().toString();
      wallet.saveKey(key, password);
      wallet.unlock(Authentication.of(key.getAddress(), password));

      assertNotNull(wallet.getCurrentAccountState());
      assertNotNull(wallet.getAccountState(wallet.getCurrentAccount()));
      assertNotNull(wallet.listServerKeyStoreAccounts());
      assertNotNull(wallet.listNodePeers());
      assertNotNull(wallet.listPeerMetrics());
      assertNotNull(wallet.getNodeStatus());
      assertNotNull(wallet.getBestBlockHeight());

      final BlockHash blockhash = wallet.getBestBlockHash();
      assertNotNull(blockhash);
      List<BlockHeader> blockHeadersByHash = wallet.listBlockHeaders(blockhash, 10);
      List<BlockHeader> blockHeadersByHeight = wallet.listBlockHeaders(blockhash, 10);
      assertEquals(blockHeadersByHash, blockHeadersByHeight);

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSaveAndUnlockAfterReload() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);

      // bind new keystore
      wallet.bindKeyStore(supplyKeyStore());

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSendWithNameOnUnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final String recipientName = randomUUID().toString().substring(0, 12).replace('-', 'a');
      wallet.createName(recipientName);
      waitForNextBlockToGenerate();

      wallet.updateName(recipientName, accountAddress);
      waitForNextBlockToGenerate();

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getCurrentAccountState();

      wallet.send(recipientName, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      wallet.send(recipientName, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      wallet.send(recipientName, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      waitForNextBlockToGenerate();

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getCurrentAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 3);

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSendWithNameOnInvalidName() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      try {
        final String recipientName = randomUUID().toString().substring(0, 12).replace('-', 'a');
        wallet.send(recipientName, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
        fail();
      } catch (Exception e) {
        // good we expected this
      }

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSendWithNameOnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final String recipientName = randomUUID().toString().substring(0, 12).replace('-', 'a');
      wallet.createName(recipientName);
      waitForNextBlockToGenerate();

      wallet.updateName(recipientName, accountAddress);
      waitForNextBlockToGenerate();

      wallet.lock(auth);
      try {
        wallet.send(accountAddress, Aer.of("100", Unit.AERGO), Fee.getDefaultFee());
        fail();
      } catch (Exception e) {
        // good we expected this
      }

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSendWithAddressOnUnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getCurrentAccountState();

      wallet.send(accountAddress, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      wallet.send(accountAddress, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      wallet.send(accountAddress, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      waitForNextBlockToGenerate();

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getCurrentAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 3);

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSendWithAddressOnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      wallet.lock(auth);

      try {
        wallet.send(accountAddress, Aer.of("100", Unit.AERGO), Fee.getDefaultFee());
        fail();
      } catch (Exception e) {
        // good we expected this
      }

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSignAndCommitOnUnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getCurrentAccountState();

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(key.getAddress())
          .to(accountAddress)
          .amount(Aer.of("100", Unit.AER))
          .nonce(wallet.incrementAndGetNonce())
          .build();

      final Transaction signed = wallet.sign(rawTransaction);

      final TxHash hash = wallet.commit(signed);
      waitForNextBlockToGenerate();
      assertNotNull(wallet.getTransaction(hash));

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getCurrentAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testUnlockAndSignAndLockAndCommit() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getCurrentAccountState();

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(key.getAddress())
          .to(accountAddress)
          .amount(Aer.of("100", Unit.AER))
          .nonce(wallet.incrementAndGetNonce())
          .build();

      final Transaction signed = wallet.sign(rawTransaction);
      wallet.lock(auth); // lock here

      final TxHash hash = wallet.commit(signed);
      waitForNextBlockToGenerate();
      assertNotNull(wallet.getTransaction(hash));

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getCurrentAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSignAndCommitOnUnlockedWithInvalidNonce() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getCurrentAccountState();

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(key.getAddress())
          .to(accountAddress)
          .amount(Aer.of("100", Unit.AER))
          .nonce(wallet.getRecentlyUsedNonce()) // wrong
          .build();

      final Transaction signed = wallet.sign(rawTransaction);

      final TxHash hash = wallet.commit(signed);
      waitForNextBlockToGenerate();
      assertNotNull(wallet.getTransaction(hash));

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getCurrentAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testSignOnLocked() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);
      wallet.lock(auth);

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(key.getAddress())
          .to(accountAddress)
          .amount(Aer.of("100", Unit.AER))
          .nonce(wallet.incrementAndGetNonce())
          .build();

      try {
        wallet.sign(rawTransaction);
        fail();
      } catch (Exception e) {
        // good we expected this
      }

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testContractOnUnlocked() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final ContractInterface contractInterface = deploy(wallet);
      execute(wallet, contractInterface);
      query(wallet, contractInterface);

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testDeployOnLocked() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);
      wallet.lock(auth);

      try {
        deploy(wallet);
        fail();
      } catch (Exception e) {
        // good we expected this
      }

      ((Closeable) wallet).close();
    }
  }

  @Test
  public void testExecuteOnLocked() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);

      wallet.unlock(auth);
      final ContractInterface contractInterface = deploy(wallet);

      try {
        wallet.lock(auth);
        execute(wallet, contractInterface);
        fail();
      } catch (Exception e) {
        // good we expected this
      }

      ((Closeable) wallet).close();
    }

  }

}
