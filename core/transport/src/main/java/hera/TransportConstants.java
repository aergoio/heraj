/*
 * @copyright defined in LICENSE.txt
 */

package hera;

public final class TransportConstants {

  /*
   * plain functions
   */

  public static final String ACCOUNT_GETSTATE = "heraj.getstate";
  public static final String ACCOUNT_SIGN = "heraj.sign";
  public static final String ACCOUNT_VERIFY = "heraj.verify";

  public static final String KEYSTORE_LIST = "heraj.list";
  public static final String KEYSTORE_CREATE = "heraj.create";
  public static final String KEYSTORE_LOCK = "heraj.lock";
  public static final String KEYSTORE_UNLOCK = "heraj.unlock";
  public static final String KEYSTORE_IMPORTKEY = "heraj.importkey";
  public static final String KEYSTORE_EXPORTKEY = "heraj.exportkey";

  public static final String BLOCK_GETBLOCK_BY_HASH = "heraj.getblock.hash";
  public static final String BLOCK_GETBLOCK_BY_HEIGHT = "heraj.getblock.height";
  public static final String BLOCK_LIST_HEADERS_BY_HASH = "heraj.listheaders.hash";
  public static final String BLOCK_LIST_HEADERS_BY_HEIGHT = "heraj.listheaders.height";

  public static final String BLOCKCHAIN_BLOCKCHAINSTATUS = "heraj.blockchainstatus";
  public static final String BLOCKCHAIN_LISTPEERS = "heraj.listpeers";
  public static final String BLOCKCHAIN_PEERMETRICS = "heraj.peermetrics";
  public static final String BLOCKCHAIN_NODESTATUS = "heraj.nodestatus";

  public static final String TRANSACTION_GETTX = "heraj.gettx";
  public static final String TRANSACTION_COMMIT = "heraj.committx";
  public static final String TRANSACTION_SEND = "heraj.sendtx";

  public static final String CONTRACT_GETRECEIPT = "heraj.getreceipt";
  public static final String CONTRACT_DEPLOY = "heraj.deploy";
  public static final String CONTRACT_GETINTERFACE = "heraj.getinterface";
  public static final String CONTRACT_EXECUTE = "heraj.execute";
  public static final String CONTRACT_QUERY = "heraj.query";



  /*
   * either functions
   */

  private static final String EITHER_POSTFIX = ".either";

  public static final String ACCOUNT_GETSTATE_EITHER = ACCOUNT_GETSTATE + EITHER_POSTFIX;
  public static final String ACCOUNT_SIGN_EITHER = ACCOUNT_SIGN + EITHER_POSTFIX;
  public static final String ACCOUNT_VERIFY_EITHER = ACCOUNT_VERIFY + EITHER_POSTFIX;

  public static final String KEYSTORE_LIST_EITHER = KEYSTORE_LIST + EITHER_POSTFIX;
  public static final String KEYSTORE_CREATE_EITHER = KEYSTORE_CREATE + EITHER_POSTFIX;
  public static final String KEYSTORE_LOCK_EITHER = KEYSTORE_LOCK + EITHER_POSTFIX;
  public static final String KEYSTORE_UNLOCK_EITHER = KEYSTORE_UNLOCK + EITHER_POSTFIX;
  public static final String KEYSTORE_IMPORTKEY_EITHER = KEYSTORE_IMPORTKEY + EITHER_POSTFIX;
  public static final String KEYSTORE_EXPORTKEY_EITHER = KEYSTORE_EXPORTKEY + EITHER_POSTFIX;

  public static final String BLOCK_GETBLOCK_BY_HASH_EITHER =
      BLOCK_GETBLOCK_BY_HASH + EITHER_POSTFIX;
  public static final String BLOCK_GETBLOCK_BY_HEIGHT_EITHER =
      BLOCK_GETBLOCK_BY_HEIGHT + EITHER_POSTFIX;
  public static final String BLOCK_LIST_HEADERS_BY_HASH_EITHER =
      BLOCK_LIST_HEADERS_BY_HASH + EITHER_POSTFIX;
  public static final String BLOCK_LIST_HEADERS_BY_HEIGHT_EITHER =
      BLOCK_LIST_HEADERS_BY_HEIGHT + EITHER_POSTFIX;

  public static final String BLOCKCHAIN_BLOCKCHAINSTATUS_EITHER =
      BLOCKCHAIN_BLOCKCHAINSTATUS + EITHER_POSTFIX;
  public static final String BLOCKCHAIN_LISTPEERS_EITHER = BLOCKCHAIN_LISTPEERS + EITHER_POSTFIX;
  public static final String BLOCKCHAIN_PEERMETRICS_EITHER =
      BLOCKCHAIN_PEERMETRICS + EITHER_POSTFIX;
  public static final String BLOCKCHAIN_NODESTATUS_EITHER = BLOCKCHAIN_NODESTATUS + EITHER_POSTFIX;

  public static final String TRANSACTION_GETTX_EITHER = TRANSACTION_GETTX + EITHER_POSTFIX;
  public static final String TRANSACTION_COMMIT_EITHER = TRANSACTION_COMMIT + EITHER_POSTFIX;
  public static final String TRANSACTION_SEND_EITHER = TRANSACTION_SEND + EITHER_POSTFIX;

  public static final String CONTRACT_GETRECEIPT_EITHER = CONTRACT_GETRECEIPT + EITHER_POSTFIX;
  public static final String CONTRACT_DEPLOY_EITHER = CONTRACT_DEPLOY + EITHER_POSTFIX;
  public static final String CONTRACT_GETINTERFACE_EITHER = CONTRACT_GETINTERFACE + EITHER_POSTFIX;
  public static final String CONTRACT_EXECUTE_EITHER = CONTRACT_EXECUTE + EITHER_POSTFIX;
  public static final String CONTRACT_QUERY_EITHER = CONTRACT_QUERY + EITHER_POSTFIX;



  /*
   * async functions
   */

  private static final String ASYNC_POSTFIX = ".async";

  public static final String ACCOUNT_GETSTATE_ASYNC = ACCOUNT_GETSTATE + ASYNC_POSTFIX;
  public static final String ACCOUNT_SIGN_ASYNC = ACCOUNT_SIGN + ASYNC_POSTFIX;
  public static final String ACCOUNT_VERIFY_ASYNC = ACCOUNT_VERIFY + ASYNC_POSTFIX;

  public static final String KEYSTORE_LIST_ASYNC = KEYSTORE_LIST + ASYNC_POSTFIX;
  public static final String KEYSTORE_CREATE_ASYNC = KEYSTORE_CREATE + ASYNC_POSTFIX;
  public static final String KEYSTORE_LOCK_ASYNC = KEYSTORE_LOCK + ASYNC_POSTFIX;
  public static final String KEYSTORE_UNLOCK_ASYNC = KEYSTORE_UNLOCK + ASYNC_POSTFIX;
  public static final String KEYSTORE_IMPORTKEY_ASYNC = KEYSTORE_IMPORTKEY + ASYNC_POSTFIX;
  public static final String KEYSTORE_EXPORTKEY_ASYNC = KEYSTORE_EXPORTKEY + ASYNC_POSTFIX;

  public static final String BLOCK_GETBLOCK_BY_HASH_ASYNC =
      BLOCK_GETBLOCK_BY_HASH + ASYNC_POSTFIX;
  public static final String BLOCK_GETBLOCK_BY_HEIGHT_ASYNC =
      BLOCK_GETBLOCK_BY_HEIGHT + ASYNC_POSTFIX;
  public static final String BLOCK_LIST_HEADERS_BY_HASH_ASYNC =
      BLOCK_LIST_HEADERS_BY_HASH + ASYNC_POSTFIX;
  public static final String BLOCK_LIST_HEADERS_BY_HEIGHT_ASYNC =
      BLOCK_LIST_HEADERS_BY_HEIGHT + ASYNC_POSTFIX;

  public static final String BLOCKCHAIN_BLOCKCHAINSTATUS_ASYNC =
      BLOCKCHAIN_BLOCKCHAINSTATUS + ASYNC_POSTFIX;
  public static final String BLOCKCHAIN_LISTPEERS_ASYNC = BLOCKCHAIN_LISTPEERS + ASYNC_POSTFIX;
  public static final String BLOCKCHAIN_PEERMETRICS_ASYNC = BLOCKCHAIN_PEERMETRICS + ASYNC_POSTFIX;
  public static final String BLOCKCHAIN_NODESTATUS_ASYNC = BLOCKCHAIN_NODESTATUS + ASYNC_POSTFIX;

  public static final String TRANSACTION_GETTX_ASYNC = TRANSACTION_GETTX + ASYNC_POSTFIX;
  public static final String TRANSACTION_COMMIT_ASYNC = TRANSACTION_COMMIT + ASYNC_POSTFIX;
  public static final String TRANSACTION_SEND_ASYNC = TRANSACTION_SEND + ASYNC_POSTFIX;

  public static final String CONTRACT_GETRECEIPT_ASYNC = CONTRACT_GETRECEIPT + ASYNC_POSTFIX;
  public static final String CONTRACT_DEPLOY_ASYNC = CONTRACT_DEPLOY + ASYNC_POSTFIX;
  public static final String CONTRACT_GETINTERFACE_ASYNC = CONTRACT_GETINTERFACE + ASYNC_POSTFIX;
  public static final String CONTRACT_EXECUTE_ASYNC = CONTRACT_EXECUTE + ASYNC_POSTFIX;
  public static final String CONTRACT_QUERY_ASYNC = CONTRACT_QUERY + ASYNC_POSTFIX;

}
