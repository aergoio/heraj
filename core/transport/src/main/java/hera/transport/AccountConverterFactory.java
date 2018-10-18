/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.AccountAddress;
import hera.api.model.ServerManagedAccount;
import java.util.function.Function;
import org.slf4j.Logger;
import types.AccountOuterClass;

public class AccountConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function<ServerManagedAccount, AccountOuterClass.Account> domainConverter =
      domainAccount -> {
        logger.trace("Domain account: {}", domainAccount);
        return AccountOuterClass.Account.newBuilder()
            .setAddress(accountAddressConverter.convertToRpcModel(domainAccount.getAddress()))
            .build();
      };

  protected final Function<AccountOuterClass.Account, ServerManagedAccount> rpcConverter =
      rpcAccount -> {
        logger.trace("Rpc account: {}", rpcAccount);
        return ServerManagedAccount
            .of(accountAddressConverter.convertToDomainModel(rpcAccount.getAddress()));
      };

  public ModelConverter<ServerManagedAccount, AccountOuterClass.Account> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
