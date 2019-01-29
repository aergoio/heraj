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
import java.io.Closeable;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public interface LookupClient extends Closeable {

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
   * Get staking information of {@code account}.
   *
   * @param account an account to check staking information
   * @return a staking information
   */
  StakingInfo getStakingInfo(Account account);

  /**
   * Get staking information of {@code accountAddress}.
   *
   * @param accountAddress an account address to check staking information
   * @return a staking information
   */
  StakingInfo getStakingInfo(AccountAddress accountAddress);

  /**
   * Get 23 elected block producer from the top voted. Note that this is just a voting result. Not
   * really working block block producers since block producers are renewed after one round.
   *
   * @return elected block producer list
   */
  List<BlockProducer> listElectedBlockProducers();

  /**
   * Get elected block producer from the top voted. Note that this is just a voting result. Not
   * really working block block producers since block producers are renewed after one round.
   *
   * @param showCount show count
   * @return elected block producer list
   */
  List<BlockProducer> listElectedBlockProducers(long showCount);

  /**
   * Get votes which {@code account} votes for.
   *
   * @param account an account
   * @return votes list
   */
  List<VotingInfo> listVotesOf(Account account);

  /**
   * Get votes which {@code accountAddress} votes for.
   *
   * @param accountAddress an account address
   * @return votes list
   */
  List<VotingInfo> listVotesOf(AccountAddress accountAddress);

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
   * Get status of current node.
   *
   * @return node status
   */
  NodeStatus getNodeStatus();

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
   * Get block headers of {@code size} backward starting from block for provided hash.
   *
   * @param blockHash block hash
   * @param size block list size whose upper bound is 1000
   * @return block list
   */
  List<BlockHeader> listBlockHeaders(BlockHash blockHash, int size);

  /**
   * Get block headers of {@code size} backward starting from block for provided height.
   *
   * @param height block's height
   * @param size block list size whose upper bound is 1000
   * @return block list
   */
  List<BlockHeader> listBlockHeaders(long height, int size);

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
   * {@inheritDoc}
   */
  void close();

}
