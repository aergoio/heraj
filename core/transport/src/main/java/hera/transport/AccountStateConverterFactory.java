/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.NumberUtils.byteArrayToPositive;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import org.slf4j.Logger;
import types.Blockchain;

public class AccountStateConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<AccountState, Blockchain.State> domainConverter =
      new Function1<AccountState, Blockchain.State>() {

        @Override
        public Blockchain.State apply(AccountState domainAccountState) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.State, AccountState> rpcConverter =
      new Function1<Blockchain.State, AccountState>() {

        @Override
        public AccountState apply(final Blockchain.State rpcAccountState) {
          logger.trace("Rpc account state: {}", rpcAccountState);
          return new AccountState(AccountAddress.of(BytesValue.EMPTY),
              rpcAccountState.getNonce(),
              new Aer(byteArrayToPositive(rpcAccountState.getBalance().toByteArray())));
        }
      };

  public ModelConverter<AccountState, Blockchain.State> create() {
    return new ModelConverter<AccountState, Blockchain.State>(domainConverter, rpcConverter);
  }

}
