/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.List;

public interface LookupClient {

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
   * Get account list on a key store.
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

}