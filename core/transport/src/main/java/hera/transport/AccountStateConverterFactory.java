/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class AccountStateConverterFactory {
  protected final transient Logger logger = getLogger(getClass());

  protected final Function<Account, Blockchain.State> domainConverter = domainAccountState -> {
    throw new UnsupportedOperationException();
  };

  protected final Function<Blockchain.State, Account> rpcConverter = rpcAccountState -> {
    logger.trace("Blockchain status: {}", rpcAccountState);
    final Account domainAccount = new Account();
    domainAccount.setNonce(rpcAccountState.getNonce());
    domainAccount.setBalance(rpcAccountState.getBalance());
    return domainAccount;
  };

  public ModelConverter<Account, Blockchain.State> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
