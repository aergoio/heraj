/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl;

import hera.api.model.Transaction;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends Closeable {
  List<SecuredAccount> list() throws IOException;

  Optional<SecuredAccount> find(String address) throws IOException;

  SecuredAccount create(String password) throws Exception;

  boolean delete(final String encodedAddress) throws IOException;

  void save(SecuredAccount account) throws IOException;

  void unlock(String address, String password) throws IOException;

  void lock(String address, String password) throws IOException;

  void sendTransaction(Transaction transaction) throws IOException;
}
