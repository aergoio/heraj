/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import java.util.function.Function;
import org.slf4j.Logger;
import types.Blockchain.State;

public class AccountStateConverterFactory {

  protected final Logger logger = getLogger(getClass());

  protected final Function<AccountState, State> domainConverter =
      domainAccountState -> {
        logger.trace("Domain status: {}", domainAccountState);
        return State.newBuilder()
            .setAccount(copyFrom(domainAccountState.getAddress()))
            .setNonce(domainAccountState.getNonce())
            .setBalance(domainAccountState.getBalance())
            .build();
      };

  protected final Function<State, AccountState> rpcConverter =
      rpcAccountState -> {
        logger.trace("Blockchain status: {}", rpcAccountState);
        final AccountState domainAccountState = new AccountState();
        domainAccountState
            .setAddress(AccountAddress.of(rpcAccountState.getAccount().toByteArray()));
        domainAccountState.setNonce(rpcAccountState.getNonce());
        domainAccountState.setBalance(rpcAccountState.getBalance());
        return domainAccountState;
      };

  public ModelConverter<AccountState, State> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
