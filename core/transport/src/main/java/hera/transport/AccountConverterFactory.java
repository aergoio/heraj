/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import java.util.function.Function;
import org.slf4j.Logger;
import types.AccountOuterClass;

public class AccountConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function<Account, AccountOuterClass.Account> domainConverter = domainAccount -> {
    logger.trace("Domain account: {}", domainAccount);
    return AccountOuterClass.Account.newBuilder()
        .setAddress(accountAddressConverter.convertToRpcModel(domainAccount.getAddress())).build();
  };

  protected final Function<AccountOuterClass.Account, Account> rpcConverter = rpcAccount -> {
    logger.trace("Rpc account: {}", rpcAccount);
    final Account account = new Account();
    account.setAddress(accountAddressConverter.convertToDomainModel(rpcAccount.getAddress()));
    return account;
  };

  public ModelConverter<Account, AccountOuterClass.Account> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
