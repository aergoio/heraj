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
import hera.api.tupleorerror.Function1;
import hera.client.AergoClient;
import hera.exception.CommitException;
import hera.exception.CommitException.CommitStatus;
import hera.exception.WalletException;
import hera.util.ThreadUtils;
import java.math.BigInteger;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractWallet implements Wallet {

  @Getter(value = AccessLevel.PROTECTED)
  protected final AergoClient aergoClient;

  @Getter(value = AccessLevel.PROTECTED)
  protected final int nonceRefreshCount;

  protected Account account;

  protected AbstractWallet(final AergoClient aergoClient, final int nonceRefreshCount) {
    this.aergoClient = aergoClient;
    this.nonceRefreshCount = nonceRefreshCount;
  }

  protected Account getAccount() {
    if (null == this.account) {
      throw new WalletException("An account is not set");
    }
    return this.account;
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
  public void setNonce(final long nonce) {
    getAccount().setNonce(nonce);
  }

  @Override
  public long getRecentlyUsedNonce() {
    return getAccount().getNonce();
  }

  @Override
  public TxHash send(final AccountAddress recipient, final String amount, final Fee fee) {
    return send(recipient, new BigInteger(amount), fee);
  }

  @Override
  public TxHash send(final AccountAddress recipient, final BigInteger amount, final Fee fee) {
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .sender(getAccount())
        .recipient(recipient)
        .amount(amount)
        .nonce(getAccount().incrementAndGetNonce())
        .fee(fee)
        .build();
    return commit(rawTransaction);
  }

  @Override
  public TxHash commit(final RawTransaction rawTransaction) {
    return commit(sign(rawTransaction));
  }

  @Override
  public TxHash commit(final Transaction signedTransaction) {
    if (!verify(signedTransaction)) {
      throw new WalletException("Sender is not correct");
    }

    TxHash txHash = null;
    Transaction commitTarget = signedTransaction;
    int i = 0;
    while (i < getNonceRefreshCount() && null == txHash) {
      try {
        txHash = getAergoClient().getTransactionOperation().commit(commitTarget);
        getAccount().setNonce(signedTransaction.getNonce());
      } catch (CommitException e) {
        if (isNonceRelatedException(e)) {
          syncNonceWithServer();
          commitTarget =
              sign(RawTransaction.copyOf(signedTransaction, getAccount().incrementAndGetNonce()));
        } else {
          throw e;
        }
      }
      ++i;
    }
    return txHash;
  }

  @Override
  public ContractInterface deploy(final ContractDefinition contractDefinition, final Fee fee) {
    final ContractTxHash deployTxHash = (ContractTxHash) sendContractRequest(
        n -> getAergoClient().getContractOperation().deploy(getAccount(), contractDefinition, n,
            fee));
    ThreadUtils.trySleep(1200L);
    final ContractTxReceipt receipt =
        getAergoClient().getContractOperation().getReceipt(deployTxHash);
    return getAergoClient().getContractOperation()
        .getContractInterface(receipt.getContractAddress());
  }

  @Override
  public ContractTxHash execute(final ContractInvocation contractInvocation, final Fee fee) {
    return (ContractTxHash) sendContractRequest(
        n -> getAergoClient().getContractOperation().execute(getAccount(), contractInvocation, n,
            fee));
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    return getAergoClient().getContractOperation().query(contractInvocation);
  }

  protected TxHash sendContractRequest(final Function1<Long, TxHash> requester) {
    TxHash executeTxHash = null;
    int i = 0;
    while (i < getNonceRefreshCount() && null == executeTxHash) {
      try {
        executeTxHash = requester.apply(getAccount().incrementAndGetNonce());
      } catch (CommitException e) {
        if (isNonceRelatedException(e)) {
          syncNonceWithServer();
        } else {
          throw e;
        }
      }
      ++i;
    }
    return executeTxHash;
  }

  protected boolean isNonceRelatedException(final CommitException e) {
    return e.getCommitStatus() == CommitStatus.NONCE_TOO_LOW
        || e.getCommitStatus() == CommitStatus.TX_HAS_SAME_NONCE;
  }

  protected void syncNonceWithServer() {
    synchronized (this.account) {
      getAccount().bindState(getAccountState());
    }
  }

}
