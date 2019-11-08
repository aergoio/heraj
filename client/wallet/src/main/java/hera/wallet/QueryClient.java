/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

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
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.io.Closeable;
import java.util.List;

public interface QueryClient extends Closeable {

  /**
   * Get account state by address.
   *
   * @param account an account
   * @return an account state
   */
  AccountState getAccountState(Account account);

  /**
   * Get account state by account.
   *
   * @param accountAddress account address
   * @return an account state
   */
  AccountState getAccountState(AccountAddress accountAddress);

  /**
   * Get owner of an account name.
   *
   * @param name an name of account
   * @return an account address binded with name
   */
  AccountAddress getNameOwner(String name);

  /**
   * Get owner of an account name at block number {@code blockNumber}.
   *
   * @param name an name of account
   * @param blockNumber a block number
   * @return an account address binded with name
   */
  AccountAddress getNameOwner(String name, long blockNumber);

  /**
   * Get staking information of {@code account}.
   *
   * @param account an account to check staking information
   * @return a staking information
   */
  StakeInfo getStakingInfo(Account account);

  /**
   * Get staking information of {@code accountAddress}.
   *
   * @param accountAddress an account address to check staking information
   * @return a staking information
   */
  StakeInfo getStakingInfo(AccountAddress accountAddress);

  /**
   * Get elected block producers for current round.
   *
   * @param showCount a show count
   * @return elected block producer list
   */
  List<ElectedCandidate> listElectedBps(int showCount);

  /**
   * Get elected candidates per {@code voteId} for current round.
   *
   * @param voteId a vote id
   * @param showCount a show count
   * @return elected block producer list
   */
  List<ElectedCandidate> listElected(String voteId, int showCount);

  /**
   * Get votes which {@code account} votes for.
   *
   * @param account an account
   * @return votes list
   */
  AccountTotalVote getVotesOf(Account account);

  /**
   * Get votes which {@code accountAddress} votes for.
   *
   * @param accountAddress an account address
   * @return votes list
   */
  AccountTotalVote getVotesOf(AccountAddress accountAddress);

  /**
   * Get account list on a server key store.
   *
   * @return account list
   */
  List<AccountAddress> listServerKeyStoreAccounts();

  /**
   * Get best block hash.
   *
   * @return block hash
   */
  BlockHash getBestBlockHash();

  /**
   * Get best block height.
   *
   * @return block height
   */
  long getBestBlockHeight();

  /**
   * Get chain id hash.
   *
   * @return a chain id hash
   */
  ChainIdHash getChainIdHash();

  /**
   * Get blockchain status.
   *
   * @return blockchain status
   */
  BlockchainStatus getBlockchainStatus();

  /**
   * Get chain info of current node.
   *
   * @return a chain info
   */
  ChainInfo getChainInfo();

  /**
   * Get chain stats of current node.
   *
   * @return a chain stats
   */
  ChainStats getChainStats();

  /**
   * Get blockchain peer addresses.
   *
   * @return peer addresses
   */
  List<Peer> listNodePeers();

  /**
   * Get metrics of peers.
   *
   * @return peer metrics
   */
  List<PeerMetric> listPeerMetrics();

  /**
   * Get server info of current node.
   *
   * @param categories a categories
   * @return server info
   */
  ServerInfo getServerInfo(List<String> categories);

  /**
   * Get status of current node.
   *
   * @return node status
   */
  NodeStatus getNodeStatus();

  /**
   * Get block metadata by hash.
   *
   * @param blockHash block hash
   * @return block
   */
  BlockMetadata getBlockMetadata(BlockHash blockHash);

  /**
   * Get block metadata by height.
   *
   * @param height block's height
   * @return block
   */
  BlockMetadata getBlockMetadata(long height);

  /**
   * Get block metadatas of {@code size} backward starting from block for provided hash.
   *
   * @param blockHash block hash
   * @param size block list size whose upper bound is 1000
   * @return block list
   */
  List<BlockMetadata> listBlockMetadatas(BlockHash blockHash, int size);

  /**
   * Get block metadatas of {@code size} backward starting from block for provided height.
   *
   * @param height block's height
   * @param size block list size whose upper bound is 1000
   * @return block list
   */
  List<BlockMetadata> listBlockMetadatas(long height, int size);

  /**
   * Get block by hash.
   *
   * @param blockHash block hash
   * @return block
   */
  Block getBlock(BlockHash blockHash);

  /**
   * Get block by height.
   *
   * @param height block's height
   * @return block
   */
  Block getBlock(long height);

  /**
   * Subscribe block metadata stream which is triggered everytime new block is generated.
   *
   * @param observer a stream observer which is invoked on new block metadata
   * @return a block subscription
   */
  Subscription<BlockMetadata> subscribeNewBlockMetadata(
      hera.api.model.StreamObserver<BlockMetadata> observer);

  /**
   * Subscribe block stream which is triggered everytime new block is generated.
   *
   * @param observer a stream observer which is invoked on new block
   * @return a block subscription
   */
  Subscription<Block> subscribeNewBlock(hera.api.model.StreamObserver<Block> observer);

  /**
   * Get transaction.
   *
   * @param txHash transaction's hash
   * @return transaction
   */
  Transaction getTransaction(TxHash txHash);

  /**
   * Get receipt of transaction.
   *
   * @param contractTxHash contract transaction hash
   * @return receipt of transaction
   */
  ContractTxReceipt getReceipt(ContractTxHash contractTxHash);

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return contract interface
   */
  ContractInterface getContractInterface(ContractAddress contractAddress);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @return contract result
   */
  ContractResult query(ContractInvocation contractInvocation);

  /**
   * List events corresponding with event filter.
   *
   * @param filter an event filter
   * @return event list
   */
  List<Event> listEvents(EventFilter filter);

  /**
   * Subscribe event corresponding with event filter.
   *
   * @param filter an event filter
   * @param observer a stream observer which is invoked on event
   * @return a subscription
   */
  Subscription<Event> subscribeEvent(EventFilter filter,
      hera.api.model.StreamObserver<Event> observer);

  /**
   * {@inheritDoc}
   */
  void close();

}
