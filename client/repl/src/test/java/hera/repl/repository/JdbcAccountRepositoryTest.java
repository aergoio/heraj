/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.repository;

import static hera.util.HexUtils.encode;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.api.model.Account;
import hera.api.model.Transaction;
import hera.repl.SecuredAccount;
import hera.util.HexUtils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class JdbcAccountRepositoryTest {

  protected Connection connection;

  protected PreparedStatement preparedStatement;

  protected ResultSet resultSet;

  protected JdbcAccountRepository accountRepository;

  protected final String address = encode(randomUUID().toString().getBytes());
  protected final String password = randomUUID().toString();

  protected SecuredAccount account;

  @Before
  public void setUp() throws Exception {
    connection = mock(Connection.class);
    preparedStatement = mock(PreparedStatement.class);
    resultSet = mock(ResultSet.class);

    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);

    accountRepository = new JdbcAccountRepository(() -> connection);
    account = accountRepository.create(password);
  }

  @Test
  public void testList() throws IOException {
    accountRepository.list();
  }

  @Test
  public void testFind() throws IOException {
    assertNotNull(accountRepository.find(address));
  }

  @Test
  public void testCreate() throws Exception {
    accountRepository.create(password);
  }

  @Test
  public void testDelete() throws IOException {
    accountRepository.delete(address);
  }

  @Test
  public void testSave() throws IOException {
    final SecuredAccount account = SecuredAccount.of(address, password);
    accountRepository.save(account);
  }

  @Test
  public void testUnlock() throws IOException, SQLException {
    final AtomicInteger count = new AtomicInteger();
    when(resultSet.next()).then(invocation -> 0 == count.getAndIncrement());
    when(resultSet.getString(1)).thenReturn(address);
    when(resultSet.getString(2)).thenReturn(account.getSecuredPrivateKey());
    accountRepository.unlock(address, password);
  }

  @Test
  public void testLock() throws IOException, SQLException {
    final AtomicInteger count = new AtomicInteger();
    when(resultSet.next()).then(invocation -> 0 == count.getAndIncrement());
    when(resultSet.getString(1)).thenReturn(address);
    when(resultSet.getString(2)).thenReturn(account.getSecuredPrivateKey());
    accountRepository.lock(address, password);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSendTransaction() throws IOException {
    final Transaction transaction = new Transaction();
    accountRepository.sendTransaction(transaction);
  }
}