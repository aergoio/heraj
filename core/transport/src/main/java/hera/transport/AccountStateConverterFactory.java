/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import org.slf4j.Logger;
import types.Blockchain;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountStateConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<AccountState, Blockchain.State> domainConverter =
      new Function1<AccountState, Blockchain.State>() {

        @Override
        public Blockchain.State apply(final AccountState domainAccountState) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.State, AccountState> rpcConverter =
      new Function1<Blockchain.State, AccountState>() {

        @Override
        public AccountState apply(final Blockchain.State rpcAccountState) {
          logger.trace("Rpc account state to convert: {}", rpcAccountState);
          final Aer parsedAer = parseToAer(rpcAccountState.getBalance());
          final AccountState domainAccountState = AccountState.newBuilder()
              .address(AccountAddress.EMPTY)
              .nonce(rpcAccountState.getNonce())
              .balance(parsedAer.equals(Aer.EMPTY) ? Aer.ZERO : parsedAer)
              .build();
          logger.trace("Domain account state converted: {}", domainAccountState);
          return domainAccountState;
        }
      };

  public ModelConverter<AccountState, Blockchain.State> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
