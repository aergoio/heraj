/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

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
import hera.exception.WalletException;
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
 * wallet. Remove after removing account interface.
 *
 * @author taeiklim
 *
 */
@AllArgsConstructor
public class LegacyWallet implements Wallet {

  protected final transient Logger logger = getLogger(getClass());

  protected final ExceptionConverter<WalletException> exceptionConverter =
      new WalletExceptionConverter();

  protected final WalletType type;

  protected WalletApi delegate;

  @Override
  public Account getAccount() {
    return new AccountFactory().create(delegate.getPrincipal(), delegate);
  }

  @Override
  public AccountTotalVote getVotes() {
    return delegate.queryApi().getVotesOf(delegate.getPrincipal());
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
      final WalletApiImpl origin = (WalletApiImpl) this.delegate;
      this.delegate = new WalletApiFactory().create(new JavaKeyStore(keyStore));
      this.delegate.bind(origin.aergoClient);
    }
  }

  @Override
  public void saveKey(AergoKey key, String password) {
    saveKey(key, key.getAddress(), password);
  }

  @Override
  public void saveKey(AergoKey key, Identity identity, String password) {
    ((WalletApiImpl) delegate).keyStore.save(Authentication.of(identity, password), key);
  }

  @Override
  public String exportKey(Authentication authentication) {
    return ((WalletApiImpl) delegate).keyStore.export(authentication, authentication.getPassword())
        .getEncoded();
  }

  @Override
  public List<Identity> listKeyStoreIdentities() {
    return ((WalletApiImpl) delegate).keyStore.listIdentities();
  }

  @Override
  public boolean unlock(Authentication authentication) {
    return delegate.unlock(authentication);
  }

  @Override
  public boolean lock(Authentication authentication) {
    return delegate.lock(authentication);
  }

  @Override
  public void storeKeyStore(String path, String password) {
    try {
      final WalletApiImpl walletApiImpl = (WalletApiImpl) delegate;
      if (walletApiImpl.keyStore instanceof JavaKeyStore) {
        ((JavaKeyStore) walletApiImpl.keyStore).store(path, password.toCharArray());
      }
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public AccountState getAccountState() {
    return delegate.queryApi().getAccountState(delegate.getPrincipal());
  }

  @Override
  public AccountState getAccountState(Account account) {
    return delegate.queryApi().getAccountState(account.getAddress());
  }

  @Override
  public AccountState getAccountState(AccountAddress accountAddress) {
    return delegate.queryApi().getAccountState(accountAddress);
  }

  @Override
  public AccountAddress getNameOwner(String name) {
    return delegate.queryApi().getNameOwner(name);
  }

  @Override
  public AccountAddress getNameOwner(String name, long blockNumber) {
    return delegate.queryApi().getNameOwner(name, blockNumber);
  }

  @Override
  public StakeInfo getStakingInfo() {
    return delegate.queryApi().getStakingInfo(delegate.getPrincipal());
  }

  @Override
  public StakeInfo getStakingInfo(Account account) {
    return delegate.queryApi().getStakingInfo(account.getAddress());
  }

  @Override
  public StakeInfo getStakingInfo(AccountAddress accountAddress) {
    return delegate.queryApi().getStakingInfo(accountAddress);
  }

  @Override
  public List<ElectedCandidate> listElectedBps(int showCount) {
    return delegate.queryApi().listElectedBps(showCount);
  }

  @Override
  public List<ElectedCandidate> listElected(String voteId, int showCount) {
    return delegate.queryApi().listElected(voteId, showCount);
  }

  @Override
  public AccountTotalVote getVotesOf(Account account) {
    return delegate.queryApi().getVotesOf(account.getAddress());
  }

  @Override
  public AccountTotalVote getVotesOf(AccountAddress accountAddress) {
    return delegate.queryApi().getVotesOf(accountAddress);
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    return delegate.queryApi().listServerKeyStoreAccounts();
  }

  @Override
  public BlockHash getBestBlockHash() {
    return delegate.queryApi().getBestBlockHash();
  }

  @Override
  public long getBestBlockHeight() {
    return delegate.queryApi().getBestBlockHeight();
  }

  @Override
  public ChainIdHash getChainIdHash() {
    return delegate.queryApi().getChainIdHash();
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return delegate.queryApi().getBlockchainStatus();
  }

  @Override
  public ChainInfo getChainInfo() {
    return delegate.queryApi().getChainInfo();
  }

  @Override
  public ChainStats getChainStats() {
    return delegate.queryApi().getChainStats();
  }

  @Override
  public List<Peer> listNodePeers() {
    return delegate.queryApi().listPeers();
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return delegate.queryApi().listPeerMetrics();
  }

  @Override
  public ServerInfo getServerInfo(List<String> categories) {
    return delegate.queryApi().getServerInfo(categories);
  }

  @Override
  public NodeStatus getNodeStatus() {
    return delegate.queryApi().getNodeStatus();
  }

  @Override
  public BlockMetadata getBlockMetadata(BlockHash blockHash) {
    return delegate.queryApi().getBlockMetadata(blockHash);
  }

  @Override
  public BlockMetadata getBlockMetadata(long height) {
    return delegate.queryApi().getBlockMetadata(height);
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(BlockHash blockHash, int size) {
    return delegate.queryApi().listBlockMetadatas(blockHash, size);
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(long height, int size) {
    return delegate.queryApi().listBlockMetadatas(height, size);
  }

  @Override
  public Block getBlock(BlockHash blockHash) {
    return delegate.queryApi().getBlock(blockHash);
  }

  @Override
  public Block getBlock(long height) {
    return delegate.queryApi().getBlock(height);
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      StreamObserver<BlockMetadata> observer) {
    return delegate.queryApi().subscribeNewBlockMetadata(observer);
  }

  @Override
  public Subscription<Block> subscribeNewBlock(StreamObserver<Block> observer) {
    return delegate.queryApi().subscribeNewBlock(observer);
  }

  @Override
  public Transaction getTransaction(TxHash txHash) {
    return delegate.queryApi().getTransaction(txHash);
  }

  @Override
  public ContractTxReceipt getReceipt(ContractTxHash contractTxHash) {
    return delegate.queryApi().getReceipt(contractTxHash);
  }

  @Override
  public ContractInterface getContractInterface(ContractAddress contractAddress) {
    return delegate.queryApi().getContractInterface(contractAddress);
  }

  @Override
  public ContractResult query(ContractInvocation contractInvocation) {
    return delegate.queryApi().query(contractInvocation);
  }

  @Override
  public List<Event> listEvents(EventFilter filter) {
    return delegate.queryApi().listEvents(filter);
  }

  @Override
  public Subscription<Event> subscribeEvent(EventFilter filter, StreamObserver<Event> observer) {
    return delegate.queryApi().subscribeEvent(filter, observer);
  }

  @Override
  public ChainIdHash getCachedChainIdHash() {
    final WalletApiImpl walletApiImpl = (WalletApiImpl) delegate;
    return walletApiImpl.aergoClient.getCachedChainIdHash();
  }

  @Override
  public void cacheChainIdHash() {
    final WalletApiImpl walletApiImpl = (WalletApiImpl) delegate;
    walletApiImpl.aergoClient.cacheChainIdHash(getChainIdHash());
  }

  @Override
  public void cacheChainIdHash(ChainIdHash chainIdHash) {
    final WalletApiImpl walletApiImpl = (WalletApiImpl) delegate;
    walletApiImpl.aergoClient.cacheChainIdHash(chainIdHash);
  }

  @Override
  public Account loadAccount(Authentication authentication) {
    unlock(authentication);
    return getAccount();
  }

  @Override
  public Transaction sign(RawTransaction rawTransaction) {
    return delegate.sign(rawTransaction);
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
    return delegate.transactionApi().createName(name);
  }

  @Override
  public TxHash updateName(String name, AccountAddress newOwner) {
    return delegate.transactionApi().updateName(name, newOwner);
  }

  @Override
  public TxHash stake(Aer amount) {
    return delegate.transactionApi().stake(amount);
  }

  @Override
  public TxHash unstake(Aer amount) {
    return delegate.transactionApi().stake(amount);
  }

  @Override
  public TxHash voteBp(List<String> candidates) {
    return delegate.transactionApi().voteBp(candidates);
  }

  @Override
  public TxHash vote(String voteId, List<String> candidates) {
    return delegate.transactionApi().vote(voteId, candidates);
  }

  @Override
  public TxHash send(String recipient, Aer amount) {
    return delegate.transactionApi().send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(String recipient, Aer amount, Fee fee) {
    return delegate.transactionApi().send(recipient, amount, fee);
  }

  @Override
  public TxHash send(String recipient, Aer amount, BytesValue payload) {
    return delegate.transactionApi().send(recipient, amount, Fee.ZERO, payload);
  }

  @Override
  public TxHash send(String recipient, Aer amount, Fee fee, BytesValue payload) {
    return delegate.transactionApi().send(recipient, amount, fee, payload);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount) {
    return delegate.transactionApi().send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, Fee fee) {
    return delegate.transactionApi().send(recipient, amount, fee);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, BytesValue payload) {
    return delegate.transactionApi().send(recipient, amount, Fee.ZERO, payload);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, Fee fee, BytesValue payload) {
    return delegate.transactionApi().send(recipient, amount, fee, payload);
  }

  @Override
  public TxHash commit(RawTransaction rawTransaction) {
    return delegate.transactionApi().commit(rawTransaction);
  }

  @Override
  public TxHash commit(Transaction signedTransaction) {
    return delegate.transactionApi().commit(signedTransaction);
  }

  @Override
  public ContractTxHash deploy(ContractDefinition contractDefinition) {
    return delegate.transactionApi().deploy(contractDefinition, Fee.ZERO);
  }

  @Override
  public ContractTxHash deploy(ContractDefinition contractDefinition, Fee fee) {
    return delegate.transactionApi().deploy(contractDefinition, fee);
  }

  @Override
  public ContractTxHash execute(ContractInvocation contractInvocation) {
    return delegate.transactionApi().execute(contractInvocation, Fee.ZERO);
  }

  @Override
  public ContractTxHash execute(ContractInvocation contractInvocation, Fee fee) {
    return delegate.transactionApi().execute(contractInvocation, fee);
  }

  @Override
  public void close() {
    final WalletApiImpl walletApiImpl = (WalletApiImpl) delegate;
    walletApiImpl.aergoClient.close();
  }

}
