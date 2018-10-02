/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import java.util.function.Function;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.Rpc;

public class AuthenticationConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function<Authentication, Rpc.Personal> domainConverter = domainAuthentication -> {
    logger.trace("Domain authentication: {}", domainAuthentication);
    return Rpc.Personal.newBuilder()
        .setAccount(AccountOuterClass.Account.newBuilder()
            .setAddress(
                accountAddressConverter.convertToRpcModel(domainAuthentication.getAddress()))
            .build())
        .setPassphrase(domainAuthentication.getPassword()).build();
  };

  protected final Function<Rpc.Personal, Authentication> rpcConverter = rpcAuthentication -> {
    throw new UnsupportedOperationException();
  };

  public ModelConverter<Authentication, Rpc.Personal> create() {
    return new ModelConverter<>(domainConverter, rpcConverter);
  }

}
