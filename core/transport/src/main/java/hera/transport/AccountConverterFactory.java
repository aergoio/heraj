/*
 * * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import org.slf4j.Logger;
import types.AccountOuterClass;

@ApiAudience.Private
@ApiStability.Unstable
public class AccountConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<Account, AccountOuterClass.Account> domainConverter =
      new Function1<Account, AccountOuterClass.Account>() {

        @Override
        public types.AccountOuterClass.Account apply(final Account domainAccount) {
          logger.trace("Domain account to convert: {}", domainAccount);
          final AccountOuterClass.Account rpcAccount = AccountOuterClass.Account.newBuilder()
              .setAddress(accountAddressConverter.convertToRpcModel(domainAccount.getAddress()))
              .build();
          logger.trace("Rpc account converted: {}", rpcAccount);
          return rpcAccount;
        }
      };

  protected final Function1<AccountOuterClass.Account, Account> rpcConverter =
      new Function1<AccountOuterClass.Account, Account>() {

        @Override
        public Account apply(final AccountOuterClass.Account rpcAccount) {
          logger.trace("Rpc account to convert: {}", rpcAccount);
          final Account domainAccount = new AccountFactory()
              .create(accountAddressConverter.convertToDomainModel(rpcAccount.getAddress()));
          logger.trace("Domain account converted: {}", domainAccount);
          return domainAccount;
        }
      };

  public ModelConverter<Account, AccountOuterClass.Account> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
