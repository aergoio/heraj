/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.Fee;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.RawTransaction;
import hera.api.model.ServerInfo;
import hera.api.model.Transaction;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LegacyQueryIT extends AbstractLegacyWalletIT {

  protected static AergoClient aergoClient;

  protected String deployKey = randomUUID().toString();
  protected int deployIntVal = randomUUID().toString().hashCode();
  protected String deployStringVal = randomUUID().toString();

  protected String executeFunction = "set";
  protected String executeKey = randomUUID().toString();
  protected int executeIntVal = randomUUID().toString().hashCode();
  protected String executeStringVal = randomUUID().toString();

  protected String queryFunction = "get";

  protected final Fee fee = Fee.ZERO;
  protected final TestClientFactory clientFactory = new TestClientFactory();
  protected final AergoKey rich = AergoKey
      .of("47XwJ7S49a3TdPApHwwZUyq1hUMmwRu6Ey61dQAZze8xLKJyDt7q1rQWSu4mXBkoyjYde2N5U", "1234");
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

  @Test
  public void testLookupOfAccount() throws Exception {
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    wallet.unlock(Authentication.of(key.getAddress(), password));

    assertNotNull(wallet.getAccount());
    assertNotNull(wallet.getAccountState());
    assertNotNull(wallet.getStakingInfo());
    assertNotNull(wallet.getVotes());
  }

  @Test
  public void testLookup() throws Exception {
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

    final ServerInfo serverInfo = wallet.getServerInfo(new ArrayList<String>());
    assertNotNull(serverInfo);

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

    final BlockMetadata blockMetadataByHash = wallet.getBlockMetadata(blockhash);
    assertNotNull(blockMetadataByHash);

    final BlockMetadata blockMetadataByHeight = wallet.getBlockMetadata(blockhash);
    assertNotNull(blockMetadataByHeight);

    final List<BlockMetadata> blockMetadatasByHash = wallet.listBlockMetadatas(blockhash, 10);
    assertNotNull(blockMetadatasByHash);

    final List<BlockMetadata> blockMetadatasByHeight = wallet.listBlockMetadatas(blockHeight, 10);
    assertNotNull(blockMetadatasByHeight);

    wallet.close();
  }

}
