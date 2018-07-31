/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.repository;

import hera.api.AccountOperation;
import hera.api.AergoApi;
import hera.api.TransactionOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.repl.AccountRepository;
import hera.repl.SecuredAccount;
import hera.util.HexUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerAccountRepository implements AccountRepository {

  protected final AergoApi aergoApi;

  protected SecuredAccount toSecureAccount(final Account account) {
    if (null == account) {
      return null;
    }
    try {
      final SecuredAccount securedAccount = new SecuredAccount();
      securedAccount.setEncodedAddress(account.getAddress().getEncodedValue());
      return securedAccount;
    } catch (final IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public List<SecuredAccount> list() throws IOException {
    final AccountOperation accountOperation = aergoApi.getAccountOperation();
    final List<Account> accounts = accountOperation.list();
    return accounts.stream()
        .map(this::toSecureAccount)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<SecuredAccount> find(final String encodedAddress) throws IOException {
    final AccountOperation accountOperation = aergoApi.getAccountOperation();
    return accountOperation.get(AccountAddress.of(HexUtils.decode(encodedAddress)))
        .map(this::toSecureAccount);
  }

  @Override
  public SecuredAccount create(final String password) throws Exception {
    final AccountOperation accountOperation = aergoApi.getAccountOperation();
    return toSecureAccount(accountOperation.create(password));
  }

  @Override
  public boolean delete(final String encodedAddress) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void save(final SecuredAccount account) throws IOException {
  }

  @Override
  public void unlock(String address, String password) throws IOException {
    final AccountOperation accountOperation = aergoApi.getAccountOperation();
    accountOperation.unlock(Account.of(HexUtils.decode(address), password));
  }

  @Override
  public void lock(String address, String password) throws IOException {
    final AccountOperation accountOperation = aergoApi.getAccountOperation();
    accountOperation.lock(Account.of(HexUtils.decode(address), password));
  }

  @Override
  public void sendTransaction(final Transaction transaction) throws IOException {
    final TransactionOperation transactionOperation = aergoApi.getTransactionOperation();
    final Signature signature = transactionOperation.sign(transaction);
    transaction.setSignature(signature);
    transactionOperation.commit(transaction);
  }

  @Override
  public void close() throws IOException {
    if (aergoApi instanceof Closeable) {
      ((Closeable) aergoApi).close();
    }
  }
}
