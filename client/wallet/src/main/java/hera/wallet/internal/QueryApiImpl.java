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
import hera.api.model.TxReceipt;
import hera.client.AergoClient;
import hera.exception.HerajException;
import hera.exception.WalletExceptionConverter;
import hera.util.ExceptionConverter;
import hera.wallet.QueryApi;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
public class QueryApiImpl implements QueryApi, ClientInjectable {

  @Setter
  @NonNull
  protected AergoClient client;

  protected final ExceptionConverter<HerajException> converter = new WalletExceptionConverter();

  @Override
  public AccountState getAccountState(final AccountAddress accountAddress) {
    try {
      return getClient().getAccountOperation().getState(accountAddress);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    try {
      return getClient().getAccountOperation().getNameOwner(name);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public AccountAddress getNameOwner(final String name, final long blockNumber) {
    try {
      return getClient().getAccountOperation().getNameOwner(name, blockNumber);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public StakeInfo getStakingInfo(final AccountAddress accountAddress) {
    try {
      return getClient().getAccountOperation().getStakingInfo(accountAddress);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<ElectedCandidate> listElectedBps(final int showCount) {
    try {
      return getClient().getAccountOperation().listElected("voteBP", showCount);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<ElectedCandidate> listElected(final String voteId, final int showCount) {
    try {
      return getClient().getAccountOperation().listElected(voteId, showCount);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public AccountTotalVote getVotesOf(final AccountAddress accountAddress) {
    try {
      return getClient().getAccountOperation().getVotesOf(accountAddress);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    try {
      return getClient().getKeyStoreOperation().list();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public BlockHash getBestBlockHash() {
    try {
      return getClient().getBlockchainOperation().getBlockchainStatus().getBestBlockHash();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public long getBestBlockHeight() {
    try {
      return getClient().getBlockchainOperation().getBlockchainStatus().getBestHeight();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ChainIdHash getChainIdHash() {
    try {
      return getClient().getBlockchainOperation().getBlockchainStatus().getChainIdHash();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    try {
      return getClient().getBlockchainOperation().getBlockchainStatus();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ChainInfo getChainInfo() {
    try {
      return getClient().getBlockchainOperation().getChainInfo();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ChainStats getChainStats() {
    try {
      return getClient().getBlockchainOperation().getChainStats();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<Peer> listPeers() {
    try {
      return getClient().getBlockchainOperation().listPeers();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }


  @Override
  public List<Peer> listPeers(boolean showHidden, boolean showSelf) {
    try {
      return getClient().getBlockchainOperation().listPeers(showHidden, showSelf);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    try {
      return getClient().getBlockchainOperation().listPeerMetrics();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ServerInfo getServerInfo(final List<String> categories) {
    try {
      return getClient().getBlockchainOperation().getServerInfo(categories);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public NodeStatus getNodeStatus() {
    try {
      return getClient().getBlockchainOperation().getNodeStatus();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public BlockMetadata getBlockMetadata(final BlockHash blockHash) {
    try {
      return getClient().getBlockOperation().getBlockMetadata(blockHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public BlockMetadata getBlockMetadata(final long height) {
    try {
      return getClient().getBlockOperation().getBlockMetadata(height);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final BlockHash blockHash, final int size) {
    try {
      return getClient().getBlockOperation().listBlockMetadatas(blockHash, size);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final long height, final int size) {
    try {
      return getClient().getBlockOperation().listBlockMetadatas(height, size);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    try {
      return getClient().getBlockOperation().getBlock(blockHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Block getBlock(final long height) {
    try {
      return getClient().getBlockOperation().getBlock(height);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      final StreamObserver<BlockMetadata> observer) {
    return subscribeBlockMetadata(observer);
  }

  @Override
  public Subscription<Block> subscribeNewBlock(final StreamObserver<Block> observer) {
    return subscribeBlock(observer);
  }

  @Override
  public Subscription<BlockMetadata> subscribeBlockMetadata(
      final StreamObserver<BlockMetadata> observer) {
    try {
      return getClient().getBlockOperation().subscribeNewBlockMetadata(observer);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Subscription<Block> subscribeBlock(final StreamObserver<Block> observer) {
    try {
      return getClient().getBlockOperation().subscribeNewBlock(observer);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    try {
      return getClient().getTransactionOperation().getTransaction(txHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TxReceipt getTxReceipt(final TxHash txHash) {
    try {
      return getClient().getTransactionOperation().getTxReceipt(txHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractTxReceipt getContractTxReceipt(final ContractTxHash contractTxHash) {
    return getContractTxReceipt(contractTxHash.adapt(TxHash.class));
  }

  @Override
  public ContractTxReceipt getContractTxReceipt(final TxHash txHash) {
    try {
      return getClient().getContractOperation().getContractTxReceipt(txHash);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    try {
      return getClient().getContractOperation().getContractInterface(contractAddress);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    try {
      return getClient().getContractOperation().query(contractInvocation);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<Event> listEvents(final EventFilter filter) {
    try {
      return getClient().getContractOperation().listEvents(filter);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Subscription<Event> subscribeEvent(final EventFilter filter,
      final StreamObserver<Event> observer) {
    try {
      return getClient().getContractOperation().subscribeEvent(filter, observer);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  protected AergoClient getClient() {
    if (null == this.client) {
      throw new HerajException("Aergo client isn't binded yet");
    }
    return this.client;
  }

}
