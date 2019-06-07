/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public final class TransportConstants {

  public static final String ACCOUNT_GETSTATE = "heraj.getstate";
  public static final String ACCOUNT_CREATE_NAME = "heraj.create.name";
  public static final String ACCOUNT_UPDATE_NAME = "heraj.update.name";
  public static final String ACCOUNT_GETNAMEOWNER = "heraj.getnameowner";
  public static final String ACCOUNT_STAKING = "heraj.staking";
  public static final String ACCOUNT_UNSTAKING = "heraj.unstaking";
  public static final String ACCOUNT_GETSTAKINGINFO = "heraj.getstakinginfo";
  public static final String ACCOUNT_SIGN = "heraj.account.sign";
  public static final String ACCOUNT_VERIFY = "heraj.verify";
  public static final String ACCOUNT_VOTE = "heraj.vote";
  public static final String ACCOUNT_LIST_ELECTED = "heraj.list.elected";
  public static final String ACCOUNT_VOTESOF = "heraj.votesof";

  public static final String KEYSTORE_LIST = "heraj.list";
  public static final String KEYSTORE_CREATE = "heraj.create";
  public static final String KEYSTORE_LOCK = "heraj.lock";
  public static final String KEYSTORE_UNLOCK = "heraj.unlock";
  public static final String KEYSTORE_SIGN = "heraj.sign";
  public static final String KEYSTORE_IMPORTKEY = "heraj.importkey";
  public static final String KEYSTORE_EXPORTKEY = "heraj.exportkey";

  public static final String BLOCK_GET_METADATA_BY_HASH = "heraj.getmetadata.hash";
  public static final String BLOCK_GET_METADATA_BY_HEIGHT = "heraj.getmetadata.height";
  public static final String BLOCK_LIST_METADATAS_BY_HASH = "heraj.listmetadatas.hash";
  public static final String BLOCK_LIST_METADATAS_BY_HEIGHT = "heraj.listmetadatas.height";
  public static final String BLOCK_GET_BLOCK_BY_HASH = "heraj.getblock.hash";
  public static final String BLOCK_GET_BLOCK_BY_HEIGHT = "heraj.getblock.height";

  public static final String BLOCKCHAIN_BLOCKCHAINSTATUS = "heraj.blockchainstatus";
  public static final String BLOCKCHAIN_CHAININFO = "heraj.chaininfo";
  public static final String BLOCKCHAIN_LIST_PEERS = "heraj.listpeers";
  public static final String BLOCKCHAIN_PEERMETRICS = "heraj.peermetrics";
  public static final String BLOCKCHAIN_SERVERINFO = "heraj.serverinfo";
  public static final String BLOCKCHAIN_NODESTATUS = "heraj.nodestatus";
  public static final String BLOCKCHAIN_VOTE = "heraj.vote";
  public static final String BLOCKCHAIN_LIST_ELECTED = "heraj.list.elected";
  public static final String BLOCKCHAIN_VOTESOF = "heraj.votesof";

  public static final String TRANSACTION_GETTX = "heraj.gettx";
  public static final String TRANSACTION_COMMIT = "heraj.committx";
  public static final String TRANSACTION_SEND = "heraj.sendtx";

  public static final String CONTRACT_GETRECEIPT = "heraj.getreceipt";
  public static final String CONTRACT_DEPLOY = "heraj.deploy";
  public static final String CONTRACT_GETINTERFACE = "heraj.getinterface";
  public static final String CONTRACT_EXECUTE = "heraj.execute";
  public static final String CONTRACT_QUERY = "heraj.query";
  public static final String CONTRACT_LIST_EVENT = "heraj.listevent";
  public static final String CONTRACT_SUBSCRIBE_EVENT = "heraj.subscribe.event";

  public static final long KEEP_ALIVE_INTERVAL = 300L; // seconds

}
