/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.internal.TryCountAndInterval;
import hera.api.tupleorerror.Function1;
import hera.client.AergoClient;
import hera.exception.CommitException;
import hera.exception.CommitException.CommitStatus;
import hera.exception.WalletException;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

public abstract class AbstractWallet implements Wallet {

  protected final Logger logger = getLogger(getClass());

  @Getter(value = AccessLevel.PROTECTED)
  protected final AergoClient aergoClient;

  @Getter(value = AccessLevel.PROTECTED)
  protected final TryCountAndInterval nonceRefreshTryCountAndInterval;

  protected Account account;

  protected AbstractWallet(final AergoClient aergoClient,
      final TryCountAndInterval tryCountAndInterval) {
    this.aergoClient = aergoClient;
    this.nonceRefreshTryCountAndInterval = tryCountAndInterval;
  }

  @Override
  public AccountState getAccountState() {
    return getAergoClient().getAccountOperation().getState(getAccount());
  }

  @Override
  public AccountState getAccountState(final Account account) {
    return getAergoClient().getAccountOperation().getState(account);
  }

  @Override
  public AccountState getAccountState(final AccountAddress accountAddress) {
    return getAergoClient().getAccountOperation().getState(accountAddress);
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    return getAergoClient().getKeyStoreOperation().list();
  }

  @Override
  public BlockHash getBestBlockHash() {
    return getAergoClient().getBlockchainOperation().getBlockchainStatus().getBestBlockHash();
  }

  @Override
  public long getBestBlockHeight() {
    return getAergoClient().getBlockchainOperation().getBlockchainStatus().getBestHeight();
  }

  @Override
  public List<Peer> listNodePeers() {
    return getAergoClient().getBlockchainOperation().listPeers();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return getAergoClient().getBlockchainOperation().getNodeStatus();
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    return getAergoClient().getBlockOperation().getBlock(blockHash);
  }

  @Override
  public Block getBlock(final long height) {
    return getAergoClient().getBlockOperation().getBlock(height);
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final BlockHash blockHash, final int size) {
    return getAergoClient().getBlockOperation().listBlockHeaders(blockHash, size);
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final long height, final int size) {
    return getAergoClient().getBlockOperation().listBlockHeaders(height, size);
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    return getAergoClient().getTransactionOperation().getTransaction(txHash);
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    return getAergoClient().getContractOperation().getReceipt(contractTxHash);
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    return getAergoClient().getContractOperation().getContractInterface(contractAddress);
  }

  @Override
  public AccountAddress getAddress() {
    return getAccount().getAddress();
  }

  @Override
  public Account getAccount() {
    if (null == this.account) {
      throw new WalletException("An account is not set");
    }
    return this.account;
  }

  @Override
  public long getRecentlyUsedNonce() {
    return getAccount().getNonce();
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee) {
    return send(recipient, amount, fee, BytesValue.EMPTY);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final Aer amount, final Fee fee,
      final BytesValue payload) {
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .sender(getAccount())
        .recipient(recipient)
        .amount(amount)
        .nonce(getAccount().incrementAndGetNonce())
        .fee(fee)
        .payload(payload)
        .build();

    Transaction signedTransaction = sign(rawTransaction);
    TxHash txHash = null;
    int i = 0;
    while (i <= getNonceRefreshTryCountAndInterval().getCount() && null == txHash) {
      try {
        txHash = getAergoClient().getTransactionOperation().commit(signedTransaction);
        getAccount().setNonce(signedTransaction.getNonce());
      } catch (CommitException e) {
        if (isNonceRelatedException(e)) {
          syncNonceWithServer();
          signedTransaction =
              sign(RawTransaction.copyOf(signedTransaction, getAccount().incrementAndGetNonce()));
        } else {
          throw e;
        }
      }
      getNonceRefreshTryCountAndInterval().trySleep();
      ++i;
    }
    return txHash;
  }

  @Override
  public ContractTxHash deploy(final ContractDefinition contractDefinition, final Fee fee) {
    return sendContractRequest(n -> getAergoClient().getContractOperation().deploy(getAccount(),
        contractDefinition, n, fee));
  }

  @Override
  public ContractTxHash execute(final ContractInvocation contractInvocation, final Fee fee) {
    return sendContractRequest(n -> getAergoClient().getContractOperation().execute(getAccount(),
        contractInvocation, n, fee));
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    return getAergoClient().getContractOperation().query(contractInvocation);
  }

  protected ContractTxHash sendContractRequest(final Function1<Long, ContractTxHash> requester) {
    ContractTxHash executeTxHash = null;
    int i = 0;
    while (i <= getNonceRefreshTryCountAndInterval().getCount() && null == executeTxHash) {
      try {
        executeTxHash = requester.apply(getAccount().incrementAndGetNonce());
      } catch (CommitException e) {
        if (isNonceRelatedException(e)) {
          syncNonceWithServer();
        } else {
          throw e;
        }
      }
      getNonceRefreshTryCountAndInterval().trySleep();
      ++i;
    }
    return executeTxHash;
  }

  protected boolean isNonceRelatedException(final CommitException e) {
    return e.getCommitStatus() == CommitStatus.NONCE_TOO_LOW
        || e.getCommitStatus() == CommitStatus.TX_HAS_SAME_NONCE;
  }

  protected void syncNonceWithServer() {
    getAccount().bindState(getAccountState());
  }

}
