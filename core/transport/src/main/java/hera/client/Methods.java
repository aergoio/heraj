/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

/**
 * Heraj methods. Naming rule : heraj.{operation}.xx.yy = OPERATION_XX_YY.
 */
@ApiAudience.Public
@ApiStability.Unstable
public abstract class Methods {

  public static final String ACCOUNT_STATE = "heraj.account.state";
  public static final String ACCOUNT_CREATENAMETX = "heraj.account.createnametx";
  public static final String ACCOUNT_UPDATENAMETX = "heraj.account.updatenametx";
  public static final String ACCOUNT_NAMEOWNER = "heraj.account.nameowner";
  public static final String ACCOUNT_STAKETX = "heraj.account.staketx";
  public static final String ACCOUNT_UNSTAKETX = "heraj.account.unstaketx";
  public static final String ACCOUNT_STAKEINFO = "heraj.account.stakeinfo";
  public static final String ACCOUNT_VOTETX = "heraj.account.votetx";
  public static final String ACCOUNT_LIST_ELECTED = "heraj.account.list.elected";
  public static final String ACCOUNT_VOTESOF = "heraj.account.votesof";

  public static final String KEYSTORE_LIST = "heraj.list";
  public static final String KEYSTORE_CREATE = "heraj.create";
  public static final String KEYSTORE_LOCK = "heraj.lock";
  public static final String KEYSTORE_UNLOCK = "heraj.unlock";
  public static final String KEYSTORE_SIGN = "heraj.sign";
  public static final String KEYSTORE_IMPORTKEY = "heraj.importkey";
  public static final String KEYSTORE_EXPORTKEY = "heraj.exportkey";
  public static final String KEYSTORE_SEND = "heraj.send";

  public static final String BLOCK_METADATA_BY_HASH = "heraj.block.metadata.by.hash";
  public static final String BLOCK_METADATA_BY_HEIGHT = "heraj.block.metadata.by.height";
  public static final String BLOCK_LIST_METADATAS_BY_HASH = "heraj.block.list.metadatas.by.hash";
  public static final String BLOCK_LIST_METADATAS_BY_HEIGHT = "heraj.block.list"
      + ".metadatas.by.height";
  public static final String BLOCK_BY_HASH = "heraj.block.by.hash";
  public static final String BLOCK_BY_HEIGHT = "heraj.block.by.height";
  public static final String BLOCK_SUBSCRIBE_BLOCKMETADATA = "heraj.block.subscribe.blockmetadata";
  public static final String BLOCK_SUBSCRIBE_BLOCK = "heraj.block.subscribe.block";

  public static final String BLOCKCHAIN_BLOCKCHAINSTATUS = "heraj.blockchain.blockchainstatus";
  public static final String BLOCKCHAIN_CHAININFO = "heraj.blockchain.chaininfo";
  public static final String BLOCKCHAIN_CHAINSTATS = "heraj.blockchain.chainstats";
  public static final String BLOCKCHAIN_LIST_PEERS = "heraj.blockchain.list.peers";
  public static final String BLOCKCHAIN_PEERMETRICS = "heraj.blockchain.peermetrics";
  public static final String BLOCKCHAIN_SERVERINFO = "heraj.blockchain.serverinfo";
  public static final String BLOCKCHAIN_NODESTATUS = "heraj.blockchain.nodestatus";

  public static final String TRANSACTION_TX = "heraj.transaction.tx";
  static final String TRANSACTION_IN_MEMPOOL = "heraj.transaction.in.mempool";
  static final String TRANSACTION_IN_BLOCK = "heraj.transaction.in.block";
  public static final String TRANSACTION_TXRECEIPT = "heraj.transaction.txreceipt";
  public static final String TRANSACTION_COMMIT = "heraj.transaction.commit";
  public static final String TRANSACTION_SENDTX_BY_ADDRESS = "heraj.transaction.sendtx.by.address";
  public static final String TRANSACTION_SENDTX_BY_NAME = "heraj.transaction.sendtx.by.name";

  public static final String CONTRACT_TXRECEIPT = "heraj.contract.txreceipt";
  public static final String CONTRACT_DEPLOYTX = "heraj.contract.deploytx";
  public static final String CONTRACT_REDEPLOYTX = "heraj.contract.redeploytx";
  public static final String CONTRACT_INTERFACE = "heraj.contract.interface";
  public static final String CONTRACT_EXECUTETX = "heraj.contract.executetx";
  public static final String CONTRACT_QUERY = "heraj.contract.query";
  public static final String CONTRACT_LIST_EVENT = "heraj.contract.list.event";
  public static final String CONTRACT_SUBSCRIBE_EVENT = "heraj.contract.subscribe.event";

}
