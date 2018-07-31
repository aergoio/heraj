/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.Account;
import java.util.function.Function;
import org.slf4j.Logger;
import types.AccountOuterClass;

public class AccountConverterFactory {

  protected final Logger logger = getLogger(getClass());

  protected final Function<Account, AccountOuterClass.Account> domainConverter =
      domainAccount -> {
        logger.trace("Domain status: {}", domainAccount);
        return AccountOuterClass.Account.newBuilder()
            .setAddress(ByteString.copyFrom(domainAccount.getAddress().getValue()))
            .build();
      };

  protected final Function<AccountOuterClass.Account, Account> rpcConverter =
      rpcAccount -> {
        logger.trace("Blockchain status: {}", rpcAccount);
        return Account.of(rpcAccount.getAddress().toByteArray(), null);
      };

  public ModelConverter<Account, AccountOuterClass.Account> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
