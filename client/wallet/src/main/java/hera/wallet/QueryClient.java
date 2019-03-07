/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.StakingInfo;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
import hera.exception.WalletConnectionException;
import hera.exception.WalletRpcException;
import java.io.Closeable;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface QueryClient extends Closeable {

  /**
   * Get account state by address.
   *
   * @param account an account
   * @return an account state
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  AccountState getAccountState(Account account);

  /**
   * Get account state by account.
   *
   * @param accountAddress account address
   * @return an account state
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  AccountState getAccountState(AccountAddress accountAddress);

  /**
   * Get owner of an account name.
   *
   * @param name an name of account
   * @return an account address binded with name
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  AccountAddress getNameOwner(String name);

  /**
   * Get staking information of {@code account}.
   *
   * @param account an account to check staking information
   * @return a staking information
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  StakingInfo getStakingInfo(Account account);

  /**
   * Get staking information of {@code accountAddress}.
   *
   * @param accountAddress an account address to check staking information
   * @return a staking information
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  StakingInfo getStakingInfo(AccountAddress accountAddress);

  /**
   * Get 23 elected block producer from the top voted. Note that this is just a voting result. Not
   * really working block block producers since block producers are renewed after one round.
   *
   * @return elected block producer list
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<BlockProducer> listElectedBlockProducers();

  /**
   * Get elected block producer from the top voted. Note that this is just a voting result. Not
   * really working block block producers since block producers are renewed after one round.
   *
   * @param showCount show count
   * @return elected block producer list
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<BlockProducer> listElectedBlockProducers(long showCount);

  /**
   * Get votes which {@code account} votes for.
   *
   * @param account an account
   * @return votes list
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<VotingInfo> listVotesOf(Account account);

  /**
   * Get votes which {@code accountAddress} votes for.
   *
   * @param accountAddress an account address
   * @return votes list
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<VotingInfo> listVotesOf(AccountAddress accountAddress);

  /**
   * Get account list on a server key store.
   *
   * @return account list
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<AccountAddress> listServerKeyStoreAccounts();

  /**
   * Get best block hash.
   *
   * @return block hash
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  BlockHash getBestBlockHash();

  /**
   * Get best block height.
   *
   * @return block height
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  long getBestBlockHeight();

  /**
   * Get chain info of current node.
   *
   * @return a chain info
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  ChainInfo getChainInfo();

  /**
   * Get blockchain peer addresses.
   *
   * @return peer addresses
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<Peer> listNodePeers();

  /**
   * Get metrics of peers.
   *
   * @return peer metrics
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<PeerMetric> listPeerMetrics();

  /**
   * Get status of current node.
   *
   * @return node status
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  NodeStatus getNodeStatus();

  /**
   * Get block header by hash.
   *
   * @param blockHash block hash
   * @return block
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  BlockHeader getBlockHeader(BlockHash blockHash);

  /**
   * Get block header by height.
   *
   * @param height block's height
   * @return block
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  BlockHeader getBlockHeader(long height);

  /**
   * Get block headers of {@code size} backward starting from block for provided hash.
   *
   * @param blockHash block hash
   * @param size block list size whose upper bound is 1000
   * @return block list
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<BlockHeader> listBlockHeaders(BlockHash blockHash, int size);

  /**
   * Get block headers of {@code size} backward starting from block for provided height.
   *
   * @param height block's height
   * @param size block list size whose upper bound is 1000
   * @return block list
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  List<BlockHeader> listBlockHeaders(long height, int size);

  /**
   * Get block by hash.
   *
   * @param blockHash block hash
   * @return block
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  Block getBlock(BlockHash blockHash);

  /**
   * Get block by height.
   *
   * @param height block's height
   * @return block
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  Block getBlock(long height);

  /**
   * Get transaction.
   *
   * @param txHash transaction's hash
   * @return transaction
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  Transaction getTransaction(TxHash txHash);

  /**
   * Get receipt of transaction.
   *
   * @param contractTxHash contract transaction hash
   * @return receipt of transaction
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  ContractTxReceipt getReceipt(ContractTxHash contractTxHash);

  /**
   * Get smart contract interface corresponding to contract address.
   *
   * @param contractAddress contract address
   * @return contract interface
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  ContractInterface getContractInterface(ContractAddress contractAddress);

  /**
   * Query the smart contract state by calling smart contract function.
   *
   * @param contractInvocation a contract invocation
   * @return contract result
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
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
