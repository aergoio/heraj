/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.protobuf.ByteString;
import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import org.slf4j.Logger;
import types.AccountOuterClass;
import types.Rpc;

public class AuthenticationConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final Function1<Authentication, Rpc.Personal> domainConverter =
      new Function1<Authentication, Rpc.Personal>() {

        @Override
        public Rpc.Personal apply(final Authentication domainAuthentication) {
          logger.trace("Domain authentication: {}", domainAuthentication);
          return Rpc.Personal.newBuilder()
              .setAccount(AccountOuterClass.Account.newBuilder()
                  .setAddress(
                      accountAddressConverter.convertToRpcModel(domainAuthentication.getAddress()))
                  .build())
              .setPassphrase(domainAuthentication.getPassword()).build();
        }
      };

  protected final Function1<Rpc.Personal, Authentication> rpcConverter =
      new Function1<Rpc.Personal, Authentication>() {

        @Override
        public Authentication apply(final Rpc.Personal rpcAuthentication) {
          throw new UnsupportedOperationException();
        }
      };

  public ModelConverter<Authentication, Rpc.Personal> create() {
    return new ModelConverter<Authentication, Rpc.Personal>(domainConverter, rpcConverter);
  }

}
