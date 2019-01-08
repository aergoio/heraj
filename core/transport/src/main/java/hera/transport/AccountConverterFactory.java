/*
 * * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import org.slf4j.Logger;
import types.AccountOuterClass;

public class AccountConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<Account, AccountOuterClass.Account> domainConverter =
      new Function1<Account, AccountOuterClass.Account>() {

        @Override
        public types.AccountOuterClass.Account apply(final Account domainAccount) {
          logger.trace("Domain account: {}", domainAccount);
          return AccountOuterClass.Account.newBuilder()
              .setAddress(accountAddressConverter.convertToRpcModel(domainAccount.getAddress()))
              .build();
        }
      };

  protected final Function1<AccountOuterClass.Account, Account> rpcConverter =
      new Function1<AccountOuterClass.Account, Account>() {

        @Override
        public Account apply(final AccountOuterClass.Account rpcAccount) {
          logger.trace("Rpc account: {}", rpcAccount);
          return new AccountFactory()
              .create(accountAddressConverter.convertToDomainModel(rpcAccount.getAddress()));
        }
      };

  public ModelConverter<Account, AccountOuterClass.Account> create() {
    return new ModelConverter<Account, AccountOuterClass.Account>(domainConverter, rpcConverter);
  }

}
