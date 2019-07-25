/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.internal;

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
import hera.wallet.QueryApi;
import java.util.List;
import lombok.NonNull;
import lombok.Setter;

public class QueryApiImpl implements QueryApi, ClientInjectable {

  @Setter
  @NonNull
  protected AergoClient client;

  protected final ExceptionConverter<WalletException> converter = new WalletExceptionConverter();

  QueryApiImpl() {}

  @Override
  public AccountState getAccountState(final AccountAddress accountAddress) {
    try {
      return client.getAccountOperation().getState(accountAddress);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    try {
      return client.getAccountOperation().getNameOwner(name);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public AccountAddress getNameOwner(final String name, final long blockNumber) {
    try {
      return client.getAccountOperation().getNameOwner(name, blockNumber);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public StakeInfo getStakingInfo(final AccountAddress accountAddress) {
    try {
      return client.getAccountOperation().getStakingInfo(accountAddress);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<ElectedCandidate> listElectedBps(final int showCount) {
    try {
      return client.getAccountOperation().listElected("voteBP", showCount);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<ElectedCandidate> listElected(final String voteId, final int showCount) {
    try {
      return client.getAccountOperation().listElected(voteId, showCount);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public AccountTotalVote getVotesOf(final AccountAddress accountAddress) {
    try {
      return client.getAccountOperation().getVotesOf(accountAddress);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    try {
      return client.getKeyStoreOperation().list();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public BlockHash getBestBlockHash() {
    try {
      return client.getBlockchainOperation().getBlockchainStatus().getBestBlockHash();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public long getBestBlockHeight() {
    try {
      return client.getBlockchainOperation().getBlockchainStatus().getBestHeight();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ChainIdHash getChainIdHash() {
    try {
      return client.getBlockchainOperation().getBlockchainStatus().getChainIdHash();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    try {
      return client.getBlockchainOperation().getBlockchainStatus();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ChainInfo getChainInfo() {
    try {
      return client.getBlockchainOperation().getChainInfo();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ChainStats getChainStats() {
    try {
      return client.getBlockchainOperation().getChainStats();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<Peer> listNodePeers() {
    try {
      return client.getBlockchainOperation().listPeers();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }


  @Override
  public List<Peer> listNodePeers(boolean showHidden, boolean showSelf) {
    try {
      return client.getBlockchainOperation().listPeers(showHidden, showSelf);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    try {
      return client.getBlockchainOperation().listPeerMetrics();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ServerInfo getServerInfo(final List<String> categories) {
    try {
      return client.getBlockchainOperation().getServerInfo(categories);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public NodeStatus getNodeStatus() {
    try {
      return client.getBlockchainOperation().getNodeStatus();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public BlockMetadata getBlockMetadata(final BlockHash blockHash) {
    try {
      return client.getBlockOperation().getBlockMetadata(blockHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public BlockMetadata getBlockMetadata(final long height) {
    try {
      return client.getBlockOperation().getBlockMetadata(height);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final BlockHash blockHash, final int size) {
    try {
      return client.getBlockOperation().listBlockMetadatas(blockHash, size);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final long height, final int size) {
    try {
      return client.getBlockOperation().listBlockMetadatas(height, size);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    try {
      return client.getBlockOperation().getBlock(blockHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Block getBlock(final long height) {
    try {
      return client.getBlockOperation().getBlock(height);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      final StreamObserver<BlockMetadata> observer) {
    try {
      return client.getBlockOperation().subscribeNewBlockMetadata(observer);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Subscription<Block> subscribeNewBlock(final StreamObserver<Block> observer) {
    try {
      return client.getBlockOperation().subscribeNewBlock(observer);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    try {
      return client.getTransactionOperation().getTransaction(txHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    try {
      return client.getContractOperation().getReceipt(contractTxHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    try {
      return client.getContractOperation().getContractInterface(contractAddress);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    try {
      return client.getContractOperation().query(contractInvocation);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<Event> listEvents(final EventFilter filter) {
    try {
      return client.getContractOperation().listEvents(filter);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Subscription<Event> subscribeEvent(final EventFilter filter,
      final StreamObserver<Event> observer) {
    try {
      return client.getContractOperation().subscribeEvent(filter, observer);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

}
