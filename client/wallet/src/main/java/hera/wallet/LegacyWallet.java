/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.ElectedCandidate;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.RawTransaction;
import hera.api.model.ServerInfo;
import hera.api.model.StakeInfo;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.HerajException;
import hera.exception.InvalidAuthenticationException;
import hera.exception.WalletExceptionConverter;
import hera.key.AergoKey;
import hera.key.AergoSignVerifier;
import hera.keystore.JavaKeyStore;
import hera.util.ExceptionConverter;
import java.security.KeyStore;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;

/**
 * Legacy wallet with poor interfaces. I hacked some apis of WalletApi to keep behaviors of legacy
 * wallet. Remove after account interface is removed.
 *
 * @author taeiklim
 */
@AllArgsConstructor
class LegacyWallet implements Wallet {

  protected final transient Logger logger = getLogger(getClass());

  protected final ExceptionConverter<HerajException> exceptionConverter =
      new WalletExceptionConverter();

  protected final WalletType type;

  protected WalletApiImpl walletApi;

  @Override
  public Account getAccount() {
    return new AccountFactory().create(walletApi.getPrincipal(), walletApi);
  }

  @Override
  public AccountTotalVote getVotes() {
    return walletApi.queryApi().getVotesOf(walletApi.getPrincipal());
  }

  @Override
  public long getRecentlyUsedNonce() {
    return getAccountState().getNonce();
  }

  @Override
  public long incrementAndGetNonce() {
    return getAccountState().getNonce() + 1L;
  }

  @Override
  public void bindKeyStore(KeyStore keyStore) {
    if (this.type.equals(WalletType.Secure)) {
      final WalletApiImpl origin = (WalletApiImpl) this.walletApi;
      this.walletApi = (WalletApiImpl) new WalletApiFactory().create(new JavaKeyStore(keyStore));
      this.walletApi.bind(origin.aergoClient);
    }
  }

  @Override
  public void saveKey(AergoKey key, String password) {
    saveKey(key, key.getAddress(), password);
  }

  @Override
  public void saveKey(AergoKey key, Identity identity, String password) {
    walletApi.keyStore.save(Authentication.of(identity, password), key);
  }

  @Override
  public String exportKey(Authentication authentication) {
    return walletApi.keyStore.export(authentication, authentication.getPassword())
        .getEncoded();
  }

  @Override
  public List<Identity> listKeyStoreIdentities() {
    return walletApi.keyStore.listIdentities();
  }

  @Override
  public boolean unlock(Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      synchronized (walletApi.lock) {
        walletApi.delegate = walletApi.keyStore.load(authentication);
        walletApi.authMac = walletApi.digest(authentication);
      }
      return true;
    } catch (InvalidAuthenticationException e) {
      return false;
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public boolean lock(Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      final String digested = walletApi.digest(authentication);
      synchronized (walletApi.lock) {
        if (!digested.equals(walletApi.authMac)) {
          return false;
        }

        walletApi.delegate = null;
        walletApi.authMac = null;
      }
      return true;
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public void storeKeyStore(String path, String password) {
    try {
      final WalletApiImpl walletApiImpl = (WalletApiImpl) walletApi;
      if (walletApiImpl.keyStore instanceof JavaKeyStore) {
        walletApiImpl.keyStore.store(path, password.toCharArray());
      }
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public AccountState getAccountState() {
    return walletApi.queryApi().getAccountState(walletApi.getPrincipal());
  }

  @Override
  public AccountState getAccountState(Account account) {
    return walletApi.queryApi().getAccountState(account.getAddress());
  }

  @Override
  public AccountState getAccountState(AccountAddress accountAddress) {
    return walletApi.queryApi().getAccountState(accountAddress);
  }

  @Override
  public AccountAddress getNameOwner(String name) {
    return walletApi.queryApi().getNameOwner(name);
  }

  @Override
  public AccountAddress getNameOwner(String name, long blockNumber) {
    return walletApi.queryApi().getNameOwner(name, blockNumber);
  }

  @Override
  public StakeInfo getStakingInfo() {
    return walletApi.queryApi().getStakingInfo(walletApi.getPrincipal());
  }

  @Override
  public StakeInfo getStakingInfo(Account account) {
    return walletApi.queryApi().getStakingInfo(account.getAddress());
  }

  @Override
  public StakeInfo getStakingInfo(AccountAddress accountAddress) {
    return walletApi.queryApi().getStakingInfo(accountAddress);
  }

  @Override
  public List<ElectedCandidate> listElectedBps(int showCount) {
    return walletApi.queryApi().listElectedBps(showCount);
  }

  @Override
  public List<ElectedCandidate> listElected(String voteId, int showCount) {
    return walletApi.queryApi().listElected(voteId, showCount);
  }

  @Override
  public AccountTotalVote getVotesOf(Account account) {
    return walletApi.queryApi().getVotesOf(account.getAddress());
  }

  @Override
  public AccountTotalVote getVotesOf(AccountAddress accountAddress) {
    return walletApi.queryApi().getVotesOf(accountAddress);
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    return walletApi.queryApi().listServerKeyStoreAccounts();
  }

  @Override
  public BlockHash getBestBlockHash() {
    return walletApi.queryApi().getBestBlockHash();
  }

  @Override
  public long getBestBlockHeight() {
    return walletApi.queryApi().getBestBlockHeight();
  }

  @Override
  public ChainIdHash getChainIdHash() {
    return walletApi.queryApi().getChainIdHash();
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return walletApi.queryApi().getBlockchainStatus();
  }

  @Override
  public ChainInfo getChainInfo() {
    return walletApi.queryApi().getChainInfo();
  }

  @Override
  public ChainStats getChainStats() {
    return walletApi.queryApi().getChainStats();
  }

  @Override
  public List<Peer> listNodePeers() {
    return walletApi.queryApi().listPeers();
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return walletApi.queryApi().listPeerMetrics();
  }

  @Override
  public ServerInfo getServerInfo(List<String> categories) {
    return walletApi.queryApi().getServerInfo(categories);
  }

  @Override
  public NodeStatus getNodeStatus() {
    return walletApi.queryApi().getNodeStatus();
  }

  @Override
  public BlockMetadata getBlockMetadata(BlockHash blockHash) {
    return walletApi.queryApi().getBlockMetadata(blockHash);
  }

  @Override
  public BlockMetadata getBlockMetadata(long height) {
    return walletApi.queryApi().getBlockMetadata(height);
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(BlockHash blockHash, int size) {
    return walletApi.queryApi().listBlockMetadatas(blockHash, size);
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(long height, int size) {
    return walletApi.queryApi().listBlockMetadatas(height, size);
  }

  @Override
  public Block getBlock(BlockHash blockHash) {
    return walletApi.queryApi().getBlock(blockHash);
  }

  @Override
  public Block getBlock(long height) {
    return walletApi.queryApi().getBlock(height);
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      StreamObserver<BlockMetadata> observer) {
    return walletApi.queryApi().subscribeBlockMetadata(observer);
  }

  @Override
  public Subscription<Block> subscribeNewBlock(StreamObserver<Block> observer) {
    return walletApi.queryApi().subscribeBlock(observer);
  }

  @Override
  public Transaction getTransaction(TxHash txHash) {
    return walletApi.queryApi().getTransaction(txHash);
  }

  @Override
  public ContractTxReceipt getReceipt(ContractTxHash contractTxHash) {
    return walletApi.queryApi().getContractTxReceipt(contractTxHash);
  }

  @Override
  public ContractInterface getContractInterface(ContractAddress contractAddress) {
    return walletApi.queryApi().getContractInterface(contractAddress);
  }

  @Override
  public ContractResult query(ContractInvocation contractInvocation) {
    return walletApi.queryApi().query(contractInvocation);
  }

  @Override
  public List<Event> listEvents(EventFilter filter) {
    return walletApi.queryApi().listEvents(filter);
  }

  @Override
  public Subscription<Event> subscribeEvent(EventFilter filter, StreamObserver<Event> observer) {
    return walletApi.queryApi().subscribeEvent(filter, observer);
  }

  @Override
  public ChainIdHash getCachedChainIdHash() {
    final WalletApiImpl walletApiImpl = (WalletApiImpl) walletApi;
    return walletApiImpl.aergoClient.getCachedChainIdHash();
  }

  @Override
  public void cacheChainIdHash() {
    final WalletApiImpl walletApiImpl = (WalletApiImpl) walletApi;
    walletApiImpl.aergoClient.cacheChainIdHash(getChainIdHash());
  }

  @Override
  public void cacheChainIdHash(ChainIdHash chainIdHash) {
    final WalletApiImpl walletApiImpl = (WalletApiImpl) walletApi;
    walletApiImpl.aergoClient.cacheChainIdHash(chainIdHash);
  }

  @Override
  public Account loadAccount(Authentication authentication) {
    unlock(authentication);
    return getAccount();
  }

  @Override
  public Transaction sign(RawTransaction rawTransaction) {
    return walletApi.sign(rawTransaction);
  }

  @Override
  public boolean verify(Transaction transaction) {
    try {
      logger.debug("Verify signed transaction {}", transaction);
      final AergoSignVerifier verifier = new AergoSignVerifier();
      return verifier.verify(transaction);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public TxHash createName(String name) {
    return walletApi.transactionApi().createName(Name.of(name));
  }

  @Override
  public TxHash updateName(String name, AccountAddress newOwner) {
    return walletApi.transactionApi().updateName(Name.of(name), newOwner);
  }

  @Override
  public TxHash stake(Aer amount) {
    return walletApi.transactionApi().stake(amount);
  }

  @Override
  public TxHash unstake(Aer amount) {
    return walletApi.transactionApi().stake(amount);
  }

  @Override
  public TxHash voteBp(List<String> candidates) {
    return walletApi.transactionApi().voteBp(candidates);
  }

  @Override
  public TxHash vote(String voteId, List<String> candidates) {
    return walletApi.transactionApi().vote(voteId, candidates);
  }

  @Override
  public TxHash send(String recipient, Aer amount) {
    return walletApi.transactionApi().send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(String recipient, Aer amount, Fee fee) {
    return walletApi.transactionApi().send(recipient, amount, fee);
  }

  @Override
  public TxHash send(String recipient, Aer amount, BytesValue payload) {
    return walletApi.transactionApi().send(recipient, amount, Fee.ZERO, payload);
  }

  @Override
  public TxHash send(String recipient, Aer amount, Fee fee, BytesValue payload) {
    return walletApi.transactionApi().send(recipient, amount, fee, payload);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount) {
    return walletApi.transactionApi().send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, Fee fee) {
    return walletApi.transactionApi().send(recipient, amount, fee);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, BytesValue payload) {
    return walletApi.transactionApi().send(recipient, amount, Fee.ZERO, payload);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, Fee fee, BytesValue payload) {
    return walletApi.transactionApi().send(recipient, amount, fee, payload);
  }

  @Override
  public TxHash commit(RawTransaction rawTransaction) {
    return walletApi.transactionApi().commit(rawTransaction);
  }

  @Override
  public TxHash commit(Transaction signedTransaction) {
    return walletApi.transactionApi().commit(signedTransaction);
  }

  @Override
  public ContractTxHash deploy(ContractDefinition contractDefinition) {
    return walletApi.transactionApi().deploy(contractDefinition, Fee.ZERO);
  }

  @Override
  public ContractTxHash deploy(ContractDefinition contractDefinition, Fee fee) {
    return walletApi.transactionApi().deploy(contractDefinition, fee);
  }

  @Override
  public ContractTxHash execute(ContractInvocation contractInvocation) {
    return walletApi.transactionApi().execute(contractInvocation, Fee.ZERO);
  }

  @Override
  public ContractTxHash execute(ContractInvocation contractInvocation, Fee fee) {
    return walletApi.transactionApi().execute(contractInvocation, fee);
  }

  @Override
  public void close() {
    final WalletApiImpl walletApiImpl = (WalletApiImpl) walletApi;
    walletApiImpl.aergoClient.close();
  }

}
