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
  protected WalletApiImpl walletApiImpl;
  protected PreparedWalletApiImpl preparedWalletApiImpl;

  @Override
  public Account getAccount() {
    return new AccountFactory().create(walletApiImpl.getPrincipal(), walletApiImpl);
  }

  @Override
  public AccountTotalVote getVotes() {
    return preparedWalletApiImpl.query().getVotesOf(walletApiImpl.getPrincipal());
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
      this.walletApiImpl = (WalletApiImpl) new WalletApiFactory()
          .create(new JavaKeyStore(keyStore));
      this.preparedWalletApiImpl = (PreparedWalletApiImpl) this.walletApiImpl
          .with(this.preparedWalletApiImpl.aergoClient);
    }
  }

  @Override
  public void saveKey(AergoKey key, String password) {
    saveKey(key, key.getAddress(), password);
  }

  @Override
  public void saveKey(AergoKey key, Identity identity, String password) {
    walletApiImpl.keyStore.save(Authentication.of(identity, password), key);
  }

  @Override
  public String exportKey(Authentication authentication) {
    return walletApiImpl.keyStore.export(authentication, authentication.getPassword())
        .getEncoded();
  }

  @Override
  public List<Identity> listKeyStoreIdentities() {
    return walletApiImpl.keyStore.listIdentities();
  }

  @Override
  public boolean unlock(Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      synchronized (walletApiImpl.lock) {
        walletApiImpl.proxySigner.setUnlocked(walletApiImpl.keyStore.load(authentication));
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
      return walletApiImpl.lock();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public void storeKeyStore(String path, String password) {
    try {
      if (walletApiImpl.keyStore instanceof JavaKeyStore) {
        walletApiImpl.keyStore.store(path, password.toCharArray());
      }
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public AccountState getAccountState() {
    return preparedWalletApiImpl.query().getAccountState(walletApiImpl.getPrincipal());
  }

  @Override
  public AccountState getAccountState(Account account) {
    return preparedWalletApiImpl.query().getAccountState(account.getAddress());
  }

  @Override
  public AccountState getAccountState(AccountAddress accountAddress) {
    return preparedWalletApiImpl.query().getAccountState(accountAddress);
  }

  @Override
  public AccountAddress getNameOwner(String name) {
    return preparedWalletApiImpl.query().getNameOwner(name);
  }

  @Override
  public AccountAddress getNameOwner(String name, long blockNumber) {
    return preparedWalletApiImpl.query().getNameOwner(name, blockNumber);
  }

  @Override
  public StakeInfo getStakingInfo() {
    return preparedWalletApiImpl.query().getStakingInfo(walletApiImpl.getPrincipal());
  }

  @Override
  public StakeInfo getStakingInfo(Account account) {
    return preparedWalletApiImpl.query().getStakingInfo(account.getAddress());
  }

  @Override
  public StakeInfo getStakingInfo(AccountAddress accountAddress) {
    return preparedWalletApiImpl.query().getStakingInfo(accountAddress);
  }

  @Override
  public List<ElectedCandidate> listElectedBps(int showCount) {
    return preparedWalletApiImpl.query().listElectedBps(showCount);
  }

  @Override
  public List<ElectedCandidate> listElected(String voteId, int showCount) {
    return preparedWalletApiImpl.query().listElected(voteId, showCount);
  }

  @Override
  public AccountTotalVote getVotesOf(Account account) {
    return preparedWalletApiImpl.query().getVotesOf(account.getAddress());
  }

  @Override
  public AccountTotalVote getVotesOf(AccountAddress accountAddress) {
    return preparedWalletApiImpl.query().getVotesOf(accountAddress);
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    return preparedWalletApiImpl.query().listServerKeyStoreAccounts();
  }

  @Override
  public BlockHash getBestBlockHash() {
    return preparedWalletApiImpl.query().getBestBlockHash();
  }

  @Override
  public long getBestBlockHeight() {
    return preparedWalletApiImpl.query().getBestBlockHeight();
  }

  @Override
  public ChainIdHash getChainIdHash() {
    return preparedWalletApiImpl.query().getChainIdHash();
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return preparedWalletApiImpl.query().getBlockchainStatus();
  }

  @Override
  public ChainInfo getChainInfo() {
    return preparedWalletApiImpl.query().getChainInfo();
  }

  @Override
  public ChainStats getChainStats() {
    return preparedWalletApiImpl.query().getChainStats();
  }

  @Override
  public List<Peer> listNodePeers() {
    return preparedWalletApiImpl.query().listPeers();
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return preparedWalletApiImpl.query().listPeerMetrics();
  }

  @Override
  public ServerInfo getServerInfo(List<String> categories) {
    return preparedWalletApiImpl.query().getServerInfo(categories);
  }

  @Override
  public NodeStatus getNodeStatus() {
    return preparedWalletApiImpl.query().getNodeStatus();
  }

  @Override
  public BlockMetadata getBlockMetadata(BlockHash blockHash) {
    return preparedWalletApiImpl.query().getBlockMetadata(blockHash);
  }

  @Override
  public BlockMetadata getBlockMetadata(long height) {
    return preparedWalletApiImpl.query().getBlockMetadata(height);
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(BlockHash blockHash, int size) {
    return preparedWalletApiImpl.query().listBlockMetadatas(blockHash, size);
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(long height, int size) {
    return preparedWalletApiImpl.query().listBlockMetadatas(height, size);
  }

  @Override
  public Block getBlock(BlockHash blockHash) {
    return preparedWalletApiImpl.query().getBlock(blockHash);
  }

  @Override
  public Block getBlock(long height) {
    return preparedWalletApiImpl.query().getBlock(height);
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      StreamObserver<BlockMetadata> observer) {
    return preparedWalletApiImpl.query().subscribeBlockMetadata(observer);
  }

  @Override
  public Subscription<Block> subscribeNewBlock(StreamObserver<Block> observer) {
    return preparedWalletApiImpl.query().subscribeBlock(observer);
  }

  @Override
  public Transaction getTransaction(TxHash txHash) {
    return preparedWalletApiImpl.query().getTransaction(txHash);
  }

  @Override
  public ContractTxReceipt getReceipt(ContractTxHash contractTxHash) {
    return preparedWalletApiImpl.query().getContractTxReceipt(contractTxHash);
  }

  @Override
  public ContractInterface getContractInterface(ContractAddress contractAddress) {
    return preparedWalletApiImpl.query().getContractInterface(contractAddress);
  }

  @Override
  public ContractResult query(ContractInvocation contractInvocation) {
    return preparedWalletApiImpl.query().queryContract(contractInvocation);
  }

  @Override
  public List<Event> listEvents(EventFilter filter) {
    return preparedWalletApiImpl.query().listEvents(filter);
  }

  @Override
  public Subscription<Event> subscribeEvent(EventFilter filter, StreamObserver<Event> observer) {
    return preparedWalletApiImpl.query().subscribeEvent(filter, observer);
  }

  @Override
  public ChainIdHash getCachedChainIdHash() {
    return this.preparedWalletApiImpl.aergoClient.getCachedChainIdHash();
  }

  @Override
  public void cacheChainIdHash() {
    final ChainIdHash chainIdHash = this.preparedWalletApiImpl.aergoClient.getBlockchainOperation()
        .getChainIdHash();
    this.preparedWalletApiImpl.aergoClient.cacheChainIdHash(chainIdHash);
  }

  @Override
  public void cacheChainIdHash(ChainIdHash chainIdHash) {
    this.preparedWalletApiImpl.aergoClient.cacheChainIdHash(chainIdHash);
  }

  @Override
  public Account loadAccount(Authentication authentication) {
    unlock(authentication);
    return getAccount();
  }

  @Override
  public Transaction sign(RawTransaction rawTransaction) {
    return walletApiImpl.sign(rawTransaction);
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
    return preparedWalletApiImpl.transaction().createName(Name.of(name));
  }

  @Override
  public TxHash updateName(String name, AccountAddress newOwner) {
    return preparedWalletApiImpl.transaction().updateName(Name.of(name), newOwner);
  }

  @Override
  public TxHash stake(Aer amount) {
    return preparedWalletApiImpl.transaction().stake(amount);
  }

  @Override
  public TxHash unstake(Aer amount) {
    return preparedWalletApiImpl.transaction().stake(amount);
  }

  @Override
  public TxHash voteBp(List<String> candidates) {
    return preparedWalletApiImpl.transaction().voteBp(candidates);
  }

  @Override
  public TxHash vote(String voteId, List<String> candidates) {
    return preparedWalletApiImpl.transaction().vote(voteId, candidates);
  }

  @Override
  public TxHash send(String recipient, Aer amount) {
    return preparedWalletApiImpl.transaction().send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(String recipient, Aer amount, Fee fee) {
    return preparedWalletApiImpl.transaction().send(recipient, amount, fee);
  }

  @Override
  public TxHash send(String recipient, Aer amount, BytesValue payload) {
    return preparedWalletApiImpl.transaction().send(recipient, amount, Fee.ZERO, payload);
  }

  @Override
  public TxHash send(String recipient, Aer amount, Fee fee, BytesValue payload) {
    return preparedWalletApiImpl.transaction().send(recipient, amount, fee, payload);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount) {
    return preparedWalletApiImpl.transaction().send(recipient, amount, Fee.ZERO);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, Fee fee) {
    return preparedWalletApiImpl.transaction().send(recipient, amount, fee);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, BytesValue payload) {
    return preparedWalletApiImpl.transaction().send(recipient, amount, Fee.ZERO, payload);
  }

  @Override
  public TxHash send(AccountAddress recipient, Aer amount, Fee fee, BytesValue payload) {
    return preparedWalletApiImpl.transaction().send(recipient, amount, fee, payload);
  }

  @Override
  public TxHash commit(RawTransaction rawTransaction) {
    return preparedWalletApiImpl.transaction().commit(rawTransaction);
  }

  @Override
  public TxHash commit(Transaction signedTransaction) {
    return preparedWalletApiImpl.transaction().commit(signedTransaction);
  }

  @Override
  public ContractTxHash deploy(ContractDefinition contractDefinition) {
    return preparedWalletApiImpl.transaction().deploy(contractDefinition, Fee.ZERO)
        .adapt(ContractTxHash.class);
  }

  @Override
  public ContractTxHash deploy(ContractDefinition contractDefinition, Fee fee) {
    return preparedWalletApiImpl.transaction().deploy(contractDefinition, fee)
        .adapt(ContractTxHash.class);
  }

  @Override
  public ContractTxHash execute(ContractInvocation contractInvocation) {
    return preparedWalletApiImpl.transaction().execute(contractInvocation, Fee.ZERO)
        .adapt(ContractTxHash.class);
  }

  @Override
  public ContractTxHash execute(ContractInvocation contractInvocation, Fee fee) {
    return preparedWalletApiImpl.transaction().execute(contractInvocation, fee)
        .adapt(ContractTxHash.class);
  }

  @Override
  public void close() {
    preparedWalletApiImpl.aergoClient.close();
  }

}
