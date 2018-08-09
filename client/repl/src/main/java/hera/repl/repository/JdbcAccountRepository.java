/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.repository;

import static hera.util.CryptoUtils.decryptFromAes128EcbWithBase64;
import static hera.util.CryptoUtils.encryptToAes128EcbWithBase64;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Transaction;
import hera.repl.AccountRepository;
import hera.repl.SecuredAccount;
import hera.util.CryptoUtils;
import hera.util.HexUtils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import org.slf4j.Logger;

public class JdbcAccountRepository implements AccountRepository {

  protected static final String CREATE_TABLE =
      "CREATE TABLE AERGO_ACCOUNT ( ADDRESS VARCHAR(256) PRIMARY KEY, PRIVATE_KEY VARCHAR(1024) )";

  protected static final String LIST_QUERY =
      "SELECT * FROM AERGO_ACCOUNT";

  protected static final String GET_QUERY =
      "SELECT * FROM AERGO_ACCOUNT where ADDRESS = ?";

  protected static final String DELETE_QUERY =
      "DELETE FROM AERGO_ACCOUNT WHERE ADDRESS = ?";

  protected static final String SAVE_QUERY =
      "INSERT INTO AERGO_ACCOUNT (ADDRESS, PRIVATE_KEY) VALUES (?, ?)";

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final Supplier<Connection> connectionSupplier;

  protected final HashMap<String, ECDSAKey> unlockedKeys = new HashMap<>();

  public JdbcAccountRepository(final Supplier<Connection> connectionSupplier) {
    this.connectionSupplier = connectionSupplier;
    createTable();
  }


  /**
   * Create table.
   * <p>
   * Ignore on exception. It will be expected as the table already exists.
   * </p>
   */
  public void createTable() {
    try (final Connection connection = connectionSupplier.get();
        final PreparedStatement ps = connection.prepareStatement(CREATE_TABLE)
    ) {
      ps.execute();
    } catch (final SQLException e) {
      // Ignore exception
    }
  }

  @Override
  public List<SecuredAccount> list() throws IOException {
    try (final Connection connection = connectionSupplier.get();
        final PreparedStatement ps = connection.prepareStatement(LIST_QUERY);
        final ResultSet rs = ps.executeQuery()
    ) {
      final ArrayList<SecuredAccount> accounts = new ArrayList<>();
      while (rs.next()) {
        final String encodedAddress = rs.getString(1);
        final String securedPrivateKey = rs.getString(2);
        final SecuredAccount account = new SecuredAccount();
        account.setEncodedAddress(encodedAddress);
        account.setSecuredPrivateKey(securedPrivateKey);
        accounts.add(account);
      }
      return accounts;
    } catch (final SQLException e) {
      throw new IOException(e);
    }
  }

  @Override
  public Optional<SecuredAccount> find(final String address) throws IOException {
    try (final Connection connection = connectionSupplier.get();
        final PreparedStatement ps = connection.prepareStatement(GET_QUERY);
    ) {
      ps.setString(1, address);
      try (final ResultSet rs = ps.executeQuery()) {
        logger.debug("Next =>");
        while (rs.next()) {
          logger.debug("Next =>");
          final String encodedAddress = rs.getString(1);
          final String securedPrivateKey = rs.getString(2);
          final SecuredAccount account = new SecuredAccount();
          account.setEncodedAddress(encodedAddress);
          account.setSecuredPrivateKey(securedPrivateKey);
          if (rs.next()) {
            throw new IllegalStateException();
          }
          return ofNullable(account);
        }
        return empty();
      }
    } catch (final SQLException e) {
      throw new IOException(e);
    }
  }

  @Override
  public SecuredAccount create(final String password) throws Exception {
    final SecuredAccount securedAccount = new SecuredAccount();
    final ECDSAKey key = new ECDSAKeyGenerator().create();
    final byte[] privateKeyValue = key.getPrivateKey().getEncoded();
    final byte[] publicKeyValue = key.getPublicKey().getEncoded();
    final String readablePublicKeyValue = HexUtils.encode(publicKeyValue);

    final byte[] passwordBytes = password.getBytes("UTF-8");
    final SecretKeySpec secretKeySpec = CryptoUtils.createSecret(passwordBytes, 16);
    final String secured = encryptToAes128EcbWithBase64(privateKeyValue, secretKeySpec);
    securedAccount.setSecuredPrivateKey(secured);
    securedAccount.setEncodedAddress(readablePublicKeyValue);
    save(securedAccount);
    return securedAccount;
  }

  @Override
  public boolean delete(final String encodedAddress) throws IOException {
    try (final Connection connection = connectionSupplier.get();
        final PreparedStatement ps = connection.prepareStatement(DELETE_QUERY)
    ) {
      ps.setString(1, encodedAddress);
      return 1 == ps.executeUpdate();
    } catch (final SQLException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void save(SecuredAccount account) throws IOException {
    try (final Connection connection = connectionSupplier.get();
        final PreparedStatement ps = connection.prepareStatement(SAVE_QUERY)
    ) {
      ps.setString(1, account.getEncodedAddress());
      ps.setString(2, account.getSecuredPrivateKey());
      ps.execute();
    } catch (final SQLException e) {
      throw new IOException(e);
    }
  }

  protected ECDSAKey extractPrivateKey(
      final SecuredAccount securedAccount,
      final String password)
      throws Exception {
    final String encodedPrivateKey = securedAccount.getSecuredPrivateKey();
    final byte[] passwordBytes = password.getBytes("UTF-8");
    final SecretKeySpec secretKeySpec = CryptoUtils.createSecret(passwordBytes, 16);

    final byte[] privateKey = decryptFromAes128EcbWithBase64(encodedPrivateKey, secretKeySpec);
    return new ECDSAKeyGenerator().create(privateKey);
  }

  @Override
  public void unlock(final String address, final String password) throws IOException {
    final Optional<SecuredAccount> securedAccountOptional = find(address);
    try {
      final SecuredAccount securedAccount = securedAccountOptional.get();
      final ECDSAKey key = extractPrivateKey(securedAccount, password);
      unlockedKeys.put(securedAccount.getEncodedAddress(), key);
    } catch (final Throwable ex) {
      throw new IllegalArgumentException(
          "Fail to unlock " + address + ". Check address and password", ex);
    }
  }

  @Override
  public void lock(final String address, final String password) throws IOException {
    final Optional<SecuredAccount> securedAccountOptional = find(address);
    securedAccountOptional.ifPresent(securedAccount -> {
      try {
        extractPrivateKey(securedAccount, password);
        unlockedKeys.remove(address);
      } catch (final Throwable ex) {
        throw new IllegalArgumentException(
            "Fail to lock " + address + ". Check address and password", ex);
      }
    });
  }

  @Override
  public void sendTransaction(final Transaction transaction) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws IOException {
  }
}
