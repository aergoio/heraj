/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BlockProducer;
import hera.api.model.ChainInfo;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.StakingInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
import hera.client.AergoClient;
import hera.exception.ExceptionHandler;
import hera.exception.WalletException;
import java.io.Closeable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

public abstract class QueryWallet implements QueryClient, Closeable {

  protected static final int SHOW_COUNT = 23;

  protected final Logger logger = getLogger(getClass());

  protected final ExceptionHandler handler = new ExceptionHandler();

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
      throw handler.handle(e);
    }
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    try {
      logger.debug("Get name owner of {}", name);
      return getAergoClient().getAccountOperation().getNameOwner(name);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public StakingInfo getStakingInfo(final Account account) {
    return getStakingInfo(account.getAddress());
  }

  @Override
  public StakingInfo getStakingInfo(final AccountAddress accountAddress) {
    try {
      logger.debug("Get staking info of {}", accountAddress);
      return getAergoClient().getAccountOperation().getStakingInfo(accountAddress);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public List<BlockProducer> listElectedBlockProducers() {
    return listElectedBlockProducers(SHOW_COUNT);
  }

  @Override
  public List<BlockProducer> listElectedBlockProducers(final long showCount) {
    try {
      logger.debug("List elected bps with show count: {}", showCount);
      return getAergoClient().getBlockchainOperation().listElectedBlockProducers(showCount);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public List<VotingInfo> listVotesOf(final Account account) {
    return listVotesOf(account.getAddress());
  }

  @Override
  public List<VotingInfo> listVotesOf(final AccountAddress accountAddress) {
    try {
      logger.debug("List votes of {}", accountAddress);
      return getAergoClient().getBlockchainOperation().listVotesOf(accountAddress);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    try {
      logger.debug("List server keystore stored addresses");
      return getAergoClient().getKeyStoreOperation().list();
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public BlockHash getBestBlockHash() {
    try {
      logger.debug("Get best block hash");
      return getAergoClient().getBlockchainOperation().getBlockchainStatus().getBestBlockHash();
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public long getBestBlockHeight() {
    try {
      logger.debug("Get best block height");
      return getAergoClient().getBlockchainOperation().getBlockchainStatus().getBestHeight();
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public ChainInfo getChainInfo() {
    try {
      logger.debug("Get chain info");
      return getAergoClient().getBlockchainOperation().getChainInfo();
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public List<Peer> listNodePeers() {
    try {
      logger.debug("List peers info");
      return getAergoClient().getBlockchainOperation().listPeers();
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    try {
      logger.debug("List metric of peers");
      return getAergoClient().getBlockchainOperation().listPeerMetrics();
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public NodeStatus getNodeStatus() {
    try {
      logger.debug("Get node status");
      return getAergoClient().getBlockchainOperation().getNodeStatus();
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public BlockHeader getBlockHeader(final BlockHash blockHash) {
    try {
      logger.debug("Get block header with hash: {}", blockHash);
      return getAergoClient().getBlockOperation().getBlockHeader(blockHash);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public BlockHeader getBlockHeader(final long height) {
    try {
      logger.debug("Get block header with height: {}", height);
      return getAergoClient().getBlockOperation().getBlockHeader(height);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final BlockHash blockHash, final int size) {
    try {
      logger.debug("List block headers with hash: {}, size: {}", blockHash, size);
      return getAergoClient().getBlockOperation().listBlockHeaders(blockHash, size);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final long height, final int size) {
    try {
      logger.debug("List block headers with height: {}, size: {}", height, size);
      return getAergoClient().getBlockOperation().listBlockHeaders(height, size);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    try {
      logger.debug("Get block with hash: {}", blockHash);
      return getAergoClient().getBlockOperation().getBlock(blockHash);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public Block getBlock(final long height) {
    try {
      logger.debug("Get block with height: {}", height);
      return getAergoClient().getBlockOperation().getBlock(height);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    try {
      logger.debug("Get transaction with hash: {}", txHash);
      return getAergoClient().getTransactionOperation().getTransaction(txHash);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    try {
      logger.debug("Get contract transaction receipt with hash: {}", contractTxHash);
      return getAergoClient().getContractOperation().getReceipt(contractTxHash);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    try {
      logger.debug("Get contract interface with address: {}", contractAddress);
      return getAergoClient().getContractOperation().getContractInterface(contractAddress);
    } catch (Exception e) {
      throw handler.handle(e);
    }
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    try {
      logger.debug("Query request with: {}", contractInvocation);
      return getAergoClient().getContractOperation().query(contractInvocation);
    } catch (Exception e) {
      throw handler.handle(e);
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