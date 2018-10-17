/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountState;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain;

public class AccountStateConverterFactory {
  protected final transient Logger logger = getLogger(getClass());

  protected final Function<AccountState, Blockchain.State> domainConverter = domainAccountState -> {
    throw new UnsupportedOperationException();
  };

  protected final Function<Blockchain.State, AccountState> rpcConverter = rpcAccountState -> {
    logger.trace("Rpc account state: {}", rpcAccountState);
    final AccountState domainAccountState = new AccountState();
    domainAccountState.setNonce(rpcAccountState.getNonce());
    domainAccountState.setBalance(rpcAccountState.getBalance());
    return domainAccountState;
  };

  public ModelConverter<AccountState, Blockchain.State> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
