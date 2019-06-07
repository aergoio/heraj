/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.ElectedCandidate;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.api.model.StakeInfo;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.exception.WalletException;
import hera.exception.WalletExceptionConverter;
import hera.util.ExceptionConverter;
import java.io.Closeable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

public abstract class QueryWallet implements QueryClient, Closeable {

  protected final Logger logger = getLogger(getClass());

  protected final ExceptionConverter<WalletException> exceptionConverter =
      new WalletExceptionConverter();

  @Getter(value = AccessLevel.PROTECTED)
  protected final AergoClient aergoClient;

  protected QueryWallet(final AergoClient aergoClient) {
    logger.debug("Binded client: {}", aergoClient);
    this.aergoClient = aergoClient;
  }

  @Override
  public AccountState getAccountState(final Account account) {
    return getAccountState(account.getAddress());
  }

  @Override
  public AccountState getAccountState(final AccountAddress accountAddress) {
    try {
      logger.debug("Get account state of {}", accountAddress);
      return getAergoClient().getAccountOperation().getState(accountAddress);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    try {
      logger.debug("Get name owner of {}", name);
      return getAergoClient().getAccountOperation().getNameOwner(name);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public AccountAddress getNameOwner(final String name, final long blockNumber) {
    try {
      logger.debug("Get name owner of {}", name);
      return getAergoClient().getAccountOperation().getNameOwner(name, blockNumber);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public StakeInfo getStakingInfo(final Account account) {
    return getStakingInfo(account.getAddress());
  }

  @Override
  public StakeInfo getStakingInfo(final AccountAddress accountAddress) {
    try {
      logger.debug("Get staking info of {}", accountAddress);
      return getAergoClient().getAccountOperation().getStakingInfo(accountAddress);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<ElectedCandidate> listElectedBps(final int showCount) {
    return listElected("voteBP", showCount);
  }

  @Override
  public List<ElectedCandidate> listElected(String voteId, int showCount) {
    try {
      logger.debug("List elected bps with show count: {}", showCount);
      return getAergoClient().getBlockchainOperation().listElected(voteId, showCount);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public AccountTotalVote getVotesOf(Account account) {
    return getVotesOf(account.getAddress());
  }

  @Override
  public AccountTotalVote getVotesOf(AccountAddress accountAddress) {
    try {
      logger.debug("List votes of {}", accountAddress);
      return getAergoClient().getBlockchainOperation().getVotesOf(accountAddress);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    try {
      logger.debug("List server keystore stored addresses");
      return getAergoClient().getKeyStoreOperation().list();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public BlockHash getBestBlockHash() {
    return getBlockchainStatus().getBestBlockHash();
  }

  @Override
  public long getBestBlockHeight() {
    return getBlockchainStatus().getBestHeight();
  }

  @Override
  public ChainIdHash getChainIdHash() {
    return getBlockchainStatus().getChainIdHash();
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    try {
      logger.debug("Get blockchain status");
      return getAergoClient().getBlockchainOperation().getBlockchainStatus();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ChainInfo getChainInfo() {
    try {
      logger.debug("Get chain info");
      return getAergoClient().getBlockchainOperation().getChainInfo();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ChainStats getChainStats() {
    try {
      logger.debug("Get chain stats");
      return getAergoClient().getBlockchainOperation().getChainStats();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<Peer> listNodePeers() {
    try {
      logger.debug("List peers info");
      return getAergoClient().getBlockchainOperation().listPeers();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    try {
      logger.debug("List metric of peers");
      return getAergoClient().getBlockchainOperation().listPeerMetrics();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ServerInfo getServerInfo(final List<String> categories) {
    try {
      logger.debug("Get server info");
      return getAergoClient().getBlockchainOperation().getServerInfo(categories);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public NodeStatus getNodeStatus() {
    try {
      logger.debug("Get node status");
      return getAergoClient().getBlockchainOperation().getNodeStatus();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public BlockMetadata getBlockMetadata(final BlockHash blockHash) {
    try {
      logger.debug("Get block metadata with hash: {}", blockHash);
      return getAergoClient().getBlockOperation().getBlockMetadata(blockHash);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public BlockMetadata getBlockMetadata(final long height) {
    try {
      logger.debug("Get block metadata with height: {}", height);
      return getAergoClient().getBlockOperation().getBlockMetadata(height);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final BlockHash blockHash, final int size) {
    try {
      logger.debug("List block metadatas with hash: {}, size: {}", blockHash, size);
      return getAergoClient().getBlockOperation().listBlockMetadatas(blockHash, size);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final long height, final int size) {
    try {
      logger.debug("List block metadatas with height: {}, size: {}", height, size);
      return getAergoClient().getBlockOperation().listBlockMetadatas(height, size);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    try {
      logger.debug("Get block with hash: {}", blockHash);
      return getAergoClient().getBlockOperation().getBlock(blockHash);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Block getBlock(final long height) {
    try {
      logger.debug("Get block with height: {}", height);
      return getAergoClient().getBlockOperation().getBlock(height);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      StreamObserver<BlockMetadata> observer) {
    try {
      logger.debug("Stream block metadata with observer: {}", observer);
      return getAergoClient().getBlockOperation().subscribeNewBlockMetadata(observer);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Subscription<Block> subscribeNewBlock(StreamObserver<Block> observer) {
    try {
      logger.debug("Stream block with observer: {}", observer);
      return getAergoClient().getBlockOperation().subscribeNewBlock(observer);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    try {
      logger.debug("Get transaction with hash: {}", txHash);
      return getAergoClient().getTransactionOperation().getTransaction(txHash);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    try {
      logger.debug("Get contract transaction receipt with hash: {}", contractTxHash);
      return getAergoClient().getContractOperation().getReceipt(contractTxHash);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    try {
      logger.debug("Get contract interface with address: {}", contractAddress);
      return getAergoClient().getContractOperation().getContractInterface(contractAddress);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    try {
      logger.debug("Query request with: {}", contractInvocation);
      return getAergoClient().getContractOperation().query(contractInvocation);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<Event> listEvents(EventFilter filter) {
    try {
      logger.debug("List events with filter: {}", filter);
      return getAergoClient().getContractOperation().listEvents(filter);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Subscription<Event> subscribeEvent(EventFilter filter,
      hera.api.model.StreamObserver<Event> observer) {
    try {
      logger.debug("Subscribe events with filter: {}, observer: {}", filter, observer);
      return getAergoClient().getContractOperation().subscribeEvent(filter, observer);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public void close() {
    try {
      getAergoClient().close();
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

}
