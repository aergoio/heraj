/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.it;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.ElectedCandidate;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.StreamObserver;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.UnbindedAccountException;
import hera.exception.WalletCommitException;
import hera.key.AergoKey;
import hera.model.KeyAlias;
import hera.util.IoUtils;
import hera.wallet.ServerKeyStoreWallet;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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

  protected String keyStorePath = System.getProperty("java.io.tmpdir") + ".keystore";
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
            .withNonBlockingConnect()
            .withEndpoint(hostname)
            .build(type),
        new WalletBuilder()
            .withTimeout(1000L, TimeUnit.MILLISECONDS)
            .withNonBlockingConnect()
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
    final AccountState preState = wallet.getAccountState();

    final ContractDefinition contractDefinition = ContractDefinition.newBuilder()
        .encodedContract(encodedContract)
        .constructorArgs(deployKey, deployIntVal, deployStringVal)
        .build();

    final ContractTxHash deployTxHash = wallet.deploy(contractDefinition, Fee.getDefaultFee());
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

    wallet.execute(contractInvocation, Fee.getDefaultFee());
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

  @Before
  public void setUp() throws Exception {
    super.setUp();
    this.keyStore = supplyKeyStore();
  }

  @After
  public void tearDown() {
    super.tearDown();
  }

  protected java.security.KeyStore supplyKeyStore() throws Exception {
    final java.security.KeyStore keyStore = java.security.KeyStore.getInstance("PKCS12");
    try {
      keyStore.load(new FileInputStream(keyStorePath),
          keyStorePasword.toCharArray());
      return keyStore;
    } catch (CertificateException e) {
      keyStore.load(null, keyStorePasword.toCharArray());
    } catch (IOException e) {
      keyStore.load(null, keyStorePasword.toCharArray());
    }
    return keyStore;
  }

  @Test
  public void testLookupOfAccount() throws Exception {
    for (Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      final String password = randomUUID().toString();
      wallet.saveKey(key, password);
      wallet.unlock(Authentication.of(key.getAddress(), password));

      assertNotNull(wallet.getAccount());
      assertNotNull(wallet.getAccountState());
      assertNotNull(wallet.getStakingInfo());
      assertNotNull(wallet.getVotes());
    }
  }

  @Test
  public void testLookup() throws Exception {
    for (Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      final String password = randomUUID().toString();
      wallet.saveKey(key, password);
      wallet.unlock(Authentication.of(key.getAddress(), password));

      final AccountState accountState = wallet.getAccountState(wallet.getAccount());
      assertNotNull(accountState);

      final List<AccountAddress> keyStoreAccounts = wallet.listServerKeyStoreAccounts();
      assertNotNull(keyStoreAccounts);

      final List<Peer> nodePeers = wallet.listNodePeers();
      assertNotNull(nodePeers);

      final List<PeerMetric> peerMetrics = wallet.listPeerMetrics();
      assertNotNull(peerMetrics);

      final NodeStatus nodeStatus = wallet.getNodeStatus();
      assertNotNull(nodeStatus);

      final ChainInfo chainInfo = wallet.getChainInfo();
      assertNotNull(chainInfo);

      final BlockHash blockhash = wallet.getBestBlockHash();
      assertNotNull(blockhash);

      final long blockHeight = wallet.getBestBlockHeight();
      assertNotNull(blockHeight);

      final ChainIdHash chainIdHash = wallet.getChainIdHash();
      assertNotNull(chainIdHash);

      final BlockchainStatus blockchainStatus = wallet.getBlockchainStatus();
      assertNotNull(blockchainStatus);

      final BlockHeader blockHeaderByHash = wallet.getBlockHeader(blockhash);
      assertNotNull(blockHeaderByHash);

      final BlockHeader blockHeaderByHeight = wallet.getBlockHeader(blockhash);
      assertNotNull(blockHeaderByHeight);

      final List<BlockHeader> blockHeadersByHash = wallet.listBlockHeaders(blockhash, 10);
      assertNotNull(blockHeadersByHash);

      final List<BlockHeader> blockHeadersByHeight = wallet.listBlockHeaders(blockHeight, 10);
      assertNotNull(blockHeadersByHeight);

      wallet.close();
    }
  }

  @Test
  public void testSaveAndLookupSavedAddresses() {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final int beforeSize = wallet.listKeyStoreIdentities().size();

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);

      final int afterSize = wallet.listKeyStoreIdentities().size();
      assertEquals(beforeSize + 1, afterSize);
      assertTrue(wallet.listKeyStoreIdentities().contains(key.getAddress()));

      wallet.close();
    }
  }

  @Test
  public void testSaveAndLookupSavedAddressesWithAlias() {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final int beforeSize = wallet.listKeyStoreIdentities().size();

      final String info = randomUUID().toString().toLowerCase().replace("-", "");
      final Identity identity = new KeyAlias(info);
      final AergoKey key = supplyKeyWithAergo(wallet);

      if (wallet instanceof ServerKeyStoreWallet) {
        try {
          wallet.saveKey(key, identity, password);
          fail();
        } catch (Exception e) {
          // good we expected this
        }
      } else {
        wallet.saveKey(key, identity, password);

        final int afterSize = wallet.listKeyStoreIdentities().size();
        assertEquals(beforeSize + 1, afterSize);
        assertTrue(wallet.listKeyStoreIdentities().contains(identity));
      }

      wallet.close();
    }
  }

  @Test
  public void testSaveAndUnlockAfterReload() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);
      wallet.storeKeyStore(keyStorePath, keyStorePasword);

      // bind new keystore
      wallet.bindKeyStore(supplyKeyStore());

      final Authentication auth = Authentication.of(key.getAddress(), password);
      assertTrue(wallet.unlock(auth));

      wallet.close();
    }
  }

  @Test
  public void testSendWithNameOnUnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final String recipientName = randomUUID().toString().substring(0, 12).replace('-', 'a');
      wallet.createName(recipientName);
      waitForNextBlockToGenerate();

      wallet.updateName(recipientName, accountAddress);
      waitForNextBlockToGenerate();

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getAccountState();

      wallet.send(recipientName, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      wallet.send(recipientName, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      wallet.send(recipientName, Aer.of("100", Unit.GAER), Fee.getDefaultFee());
      waitForNextBlockToGenerate();

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 3);

      wallet.close();
    }
  }

  @Test
  public void testSendWithNameOnInvalidName() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
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

      wallet.close();
    }
  }

  @Test
  public void testSendWithNameOnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
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

      wallet.close();
    }
  }

  @Test
  public void testStakeOnLocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);

      try {
        final Aer stakingAmount = Aer.of("10", Unit.AERGO);
        wallet.stake(stakingAmount);
      } catch (UnbindedAccountException e) {
        // good we expected this
      }
      wallet.close();
    }
  }

  @Test
  public void testStake() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getAccountState();

      final Aer stakingAmount = preState.getBalance();

      wallet.stake(stakingAmount);
      waitForNextBlockToGenerate();

      final StakeInfo stakingInfo = wallet.getStakingInfo();
      assertEquals(stakingAmount, stakingInfo.getAmount());

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

      wallet.close();
    }
  }

  @Test
  public void testVote() {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final AccountState preState = wallet.getAccountState();

      final Aer stakingAmount = preState.getBalance();

      wallet.stake(stakingAmount);
      waitForNextBlockToGenerate();

      wallet.voteBp(asList(peerIds));
      waitForNextBlockToGenerate();

      final List<ElectedCandidate> electedBlockProducers = wallet.listElectedBps(100);
      assertTrue(peerIds.length == electedBlockProducers.size());

      final AccountTotalVote accountTotalVote = wallet.getVotes();
      assertTrue(1 <= accountTotalVote.getVoteInfos().size());
      assertEquals(peerIds.length, accountTotalVote.getVoteInfos().get(0).getCandidateIds().size());

      wallet.close();
    }
  }

  @Test
  public void testVotingOnUnStakedOne() {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      try {
        wallet.voteBp(asList(peerIds));
        fail();
      } catch (WalletCommitException e) {
        // good we expected this
      }

      wallet.close();
    }
  }

  @Test
  public void testSendWithAddressOnUnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);

      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getAccountState();

      wallet.send(accountAddress, Aer.of("100", Unit.GAER), Fee.ZERO);
      wallet.send(accountAddress, Aer.of("100", Unit.GAER), Fee.ZERO);
      wallet.send(accountAddress, Aer.of("100", Unit.GAER), Fee.ZERO);
      waitForNextBlockToGenerate();

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 3);

      wallet.close();
    }
  }

  @Test
  public void testSendWithAddressOnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
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

      wallet.close();
    }
  }

  @Test
  public void testSignAndCommitOnUnlocked() throws IOException {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getAccountState();

      wallet.cacheChainIdHash();
      final RawTransaction rawTransaction = RawTransaction.newBuilder(wallet.getCachedChainIdHash())
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
      final AccountState postState = wallet.getAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

      wallet.close();
    }
  }

  @Test
  public void testUnlockAndSignAndLockAndCommit() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getAccountState();

      wallet.cacheChainIdHash();
      final RawTransaction rawTransaction = RawTransaction.newBuilder(wallet.getCachedChainIdHash())
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
      final AccountState postState = wallet.getAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

      wallet.close();
    }
  }

  @Test
  public void testSignAndCommitOnUnlockedWithInvalidNonce() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final long preCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState preState = wallet.getAccountState();

      wallet.cacheChainIdHash();
      final RawTransaction rawTransaction = RawTransaction.newBuilder(wallet.getCachedChainIdHash())
          .from(key.getAddress())
          .to(accountAddress)
          .amount(Aer.of("100", Unit.AER))
          .nonce(wallet.getRecentlyUsedNonce()) // wrong
          .build();

      final TxHash hash = wallet.commit(rawTransaction);
      waitForNextBlockToGenerate();
      assertNotNull(wallet.getTransaction(hash));

      final long postCachedNonce = wallet.getRecentlyUsedNonce();
      final AccountState postState = wallet.getAccountState();
      validatePreAndPostState(preState, preCachedNonce, postState, postCachedNonce, 1);

      wallet.close();
    }
  }

  @Test
  public void testSignOnLocked() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);
      wallet.lock(auth);

      final RawTransaction rawTransaction = RawTransaction.newBuilder(wallet.getCachedChainIdHash())
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

      wallet.close();
    }
  }

  @Test
  public void testContractOnUnlocked() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);
      wallet.unlock(auth);

      final ContractInterface contractInterface = deploy(wallet);
      execute(wallet, contractInterface);
      query(wallet, contractInterface);

      wallet.close();
    }
  }

  @Test
  public void testDeployOnLocked() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
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

      wallet.close();
    }
  }

  @Test
  public void testExecuteOnLocked() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
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

      wallet.close();
    }

  }

  @Test
  public void testContractEvent() throws Exception {
    for (final Wallet wallet : supplyWorkingWalletList()) {
      logger.info("Current wallet: {}", wallet);
      wallet.bindKeyStore(keyStore);

      final AergoKey key = supplyKeyWithAergo(wallet);
      wallet.saveKey(key, password);
      final Authentication auth = Authentication.of(key.getAddress(), password);

      wallet.unlock(auth);
      final ContractInterface contractInterface = deploy(wallet);
      final ContractAddress contractAddress = contractInterface.getAddress();

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

      assertEquals(0L, latch.getCount());
      List<Event> events = wallet.listEvents(eventFilter);
      assertEquals(eventCount, events.size());

      wallet.close();
    }

  }

}
